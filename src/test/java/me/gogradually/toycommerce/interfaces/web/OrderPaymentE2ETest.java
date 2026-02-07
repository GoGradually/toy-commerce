package me.gogradually.toycommerce.interfaces.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderPaymentE2ETest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCheckoutPayAndGetOrderSuccessfully() throws Exception {
        Long memberId = 7001L;
        Long productId = createProduct("주문성공 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 2);
        Long orderId = checkout(memberId);

        mockMvc.perform(post("/api/orders/{orderId}/pay", orderId)
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "CARD_OK"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.totalAmount").value(31800));

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(3));
    }

    @Test
    void shouldMarkFailedAndRestoreStockWhenPaymentFails() throws Exception {
        Long memberId = 7002L;
        Long productId = createProduct("주문실패 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 2);
        Long orderId = checkout(memberId);

        mockMvc.perform(post("/api/orders/{orderId}/pay", orderId)
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "FAIL_CARD"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PAYMENT-400-FAILED"));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAYMENT_FAILED"));

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(5));
    }

    private void addCartItem(Long memberId, Long productId, int quantity) throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddCartItemPayload(productId, quantity))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private Long checkout(Long memberId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long orderId = root.path("data").path("orderId").asLong();
        assertThat(orderId).isPositive();
        return orderId;
    }

    private Long createProduct(String name, int price, int stock, String status) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(name, price, stock, status))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long productId = root.path("data").path("id").asLong();
        assertThat(productId).isPositive();
        return productId;
    }

    @SuppressWarnings("unused")
    private void updateProduct(Long productId, String name, int price, String status) throws Exception {
        mockMvc.perform(patch("/api/admin/products/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateProductPayload(name, price, status))))
                .andExpect(status().isOk());
    }

    private record CreateProductPayload(
            String name,
            int price,
            int stock,
            String status
    ) {
    }

    private record UpdateProductPayload(
            String name,
            int price,
            String status
    ) {
    }

    private record AddCartItemPayload(
            Long productId,
            int quantity
    ) {
    }
}
