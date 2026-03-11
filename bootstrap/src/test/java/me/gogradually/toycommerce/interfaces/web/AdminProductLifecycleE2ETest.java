package me.gogradually.toycommerce.interfaces.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminProductLifecycleE2ETest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldForceSoldOutAndCancelOpenOrders() throws Exception {
        Long memberId = 8101L;
        Long productId = createProduct("강제품절 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 2);
        Long orderId = checkout(memberId);

        mockMvc.perform(patch("/api/admin/products/{productId}/force-sold-out", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                .andExpect(jsonPath("$.data.stock").value(0));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectDeletingReferencedSoldOutProduct() throws Exception {
        Long memberId = 8102L;
        Long productId = createProduct("삭제제한 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 1);
        checkout(memberId);

        mockMvc.perform(patch("/api/admin/products/{productId}/force-sold-out", productId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/products/{productId}", productId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("PRODUCT-409-DELETE"));
    }

    private void addCartItem(Long memberId, Long productId, int quantity) throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddCartItemPayload(productId, quantity))))
                .andExpect(status().isOk());
    }

    private Long checkout(Long memberId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isCreated())
                .andReturn();

        return readLong(result, "orderId");
    }

    private Long createProduct(String name, int price, int stock, String status) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateProductPayload(
                                name,
                                new BigDecimal(price),
                                stock,
                                status
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return readLong(result, "id");
    }

    private Long readLong(MvcResult result, String fieldName) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long value = root.path("data").path(fieldName).asLong();
        assertThat(value).isPositive();
        return value;
    }

    private record AddCartItemPayload(Long productId, Integer quantity) {
    }

    private record CreateProductPayload(String name, BigDecimal price, Integer stock, String status) {
    }
}
