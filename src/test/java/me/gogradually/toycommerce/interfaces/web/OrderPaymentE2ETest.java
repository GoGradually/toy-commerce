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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderPaymentE2ETest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCheckoutCompleteDetailsPayAndGetOrderSuccessfully() throws Exception {
        Long memberId = 7001L;
        Long productId = createProduct("주문성공 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 2);
        Long orderId = checkout(memberId);

        completeOrderDetails(memberId, orderId, "WELCOME10");

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
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.replacementOrderId").value(nullValue()));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.originalAmount").value(31800))
                .andExpect(jsonPath("$.data.discountAmount").value(3180))
                .andExpect(jsonPath("$.data.totalAmount").value(28620))
                .andExpect(jsonPath("$.data.orderDetails.couponCode").value("WELCOME10"));

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(3));

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty());
    }

    @Test
    void shouldCreateReplacementOrderAndKeepCartWhenPaymentFails() throws Exception {
        Long memberId = 7002L;
        Long productId = createProduct("주문실패 테스트 상품", 15900, 5, "ACTIVE");

        addCartItem(memberId, productId, 2);
        Long failedOrderId = checkout(memberId);
        completeOrderDetails(memberId, failedOrderId, "WELCOME10");

        MvcResult payResult = mockMvc.perform(post("/api/orders/{orderId}/pay", failedOrderId)
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "FAIL_CARD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PAYMENT_FAILED"))
                .andExpect(jsonPath("$.data.paid").value(false))
                .andExpect(jsonPath("$.data.paymentResult").value("FAILED"))
                .andReturn();

        Long replacementOrderId = readOrderId(payResult, "replacementOrderId");
        assertThat(replacementOrderId).isNotNull();

        mockMvc.perform(get("/api/orders/{orderId}", failedOrderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAYMENT_FAILED"));

        mockMvc.perform(get("/api/orders/{orderId}", replacementOrderId)
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andExpect(jsonPath("$.data.discountAmount").value(0))
                .andExpect(jsonPath("$.data.totalAmount").value(31800))
                .andExpect(jsonPath("$.data.orderDetails.receiverName").value("홍길동"))
                .andExpect(jsonPath("$.data.orderDetails.couponCode").value("WELCOME10"))
                .andExpect(jsonPath("$.data.orderDetails.paymentMethod").value("CARD"));

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(3));

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].productId").value(productId))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2));

        completeOrderDetails(memberId, replacementOrderId, "WELCOME10");

        mockMvc.perform(post("/api/orders/{orderId}/pay", replacementOrderId)
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "CARD_OK"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Member-Id", String.valueOf(memberId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items").isEmpty());

        mockMvc.perform(get("/api/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(3));
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
                .andExpect(jsonPath("$.data.status").value("CREATED"))
                .andReturn();

        return readOrderId(result, "orderId");
    }

    private Long readOrderId(MvcResult result, String fieldName) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode field = root.path("data").path(fieldName);
        assertThat(field.isMissingNode()).isFalse();
        assertThat(field.isNull()).isFalse();
        long orderId = field.asLong();
        assertThat(orderId).isPositive();
        return orderId;
    }

    private void completeOrderDetails(Long memberId, Long orderId, String couponCode) throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/details", orderId)
                        .header("X-Member-Id", String.valueOf(memberId))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CompleteOrderDetailsPayload(
                                "홍길동",
                                "01012345678",
                                "06236",
                                "서울특별시 강남구 테헤란로 123",
                                "101동 202호",
                                couponCode,
                                "CARD"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INFO_COMPLETED"));
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

    private record CreateProductPayload(
            String name,
            int price,
            int stock,
            String status
    ) {
    }

    private record AddCartItemPayload(
            Long productId,
            int quantity
    ) {
    }

    private record CompleteOrderDetailsPayload(
            String receiverName,
            String receiverPhone,
            String zipCode,
            String addressLine1,
            String addressLine2,
            String couponCode,
            String paymentMethod
    ) {
    }
}
