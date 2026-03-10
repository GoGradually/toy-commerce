package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.order.OrderService;
import me.gogradually.toycommerce.application.order.dto.*;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.PaymentMethod;
import me.gogradually.toycommerce.domain.order.exception.OrderNotFoundException;
import me.gogradually.toycommerce.interfaces.utils.GlobalExceptionHandler;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import me.gogradually.toycommerce.interfaces.utils.exception.ValidationErrorMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCheckoutOrder() throws Exception {
        CheckoutOrderInfo info = new CheckoutOrderInfo(
                1L,
                OrderStatus.CREATED,
                new BigDecimal("31800"),
                List.of(new OrderItemInfo(11L, "레고 스타터 세트", new BigDecimal("15900"), 2, new BigDecimal("31800")))
        );
        when(orderService.checkout(1001L)).thenReturn(info);

        mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Member-Id", "1001"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    void shouldCompleteOrderDetails() throws Exception {
        CompleteOrderDetailsInfo info = new CompleteOrderDetailsInfo(
                1L,
                OrderStatus.INFO_COMPLETED,
                new BigDecimal("31800"),
                new BigDecimal("3180"),
                new BigDecimal("28620"),
                "WELCOME10",
                PaymentMethod.CARD
        );
        when(orderService.completeOrderDetails(eq(1001L), eq(1L), any())).thenReturn(info);

        mockMvc.perform(post("/api/orders/1/details")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverName": "홍길동",
                                  "receiverPhone": "01012345678",
                                  "zipCode": "06236",
                                  "addressLine1": "서울특별시 강남구 테헤란로 123",
                                  "addressLine2": "101동 202호",
                                  "couponCode": "WELCOME10",
                                  "paymentMethod": "CARD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INFO_COMPLETED"))
                .andExpect(jsonPath("$.data.discountAmount").value(3180));
    }

    @Test
    void shouldPayOrder() throws Exception {
        PayOrderInfo info = new PayOrderInfo(1L, OrderStatus.PAID, true, PaymentResult.SUCCESS, null);
        when(orderService.pay(eq(1001L), eq(1L), any())).thenReturn(info);

        mockMvc.perform(post("/api/orders/1/pay")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "CARD_OK"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paid").value(true))
                .andExpect(jsonPath("$.data.paymentResult").value("SUCCESS"))
                .andExpect(jsonPath("$.data.replacementOrderId").value(nullValue()));
    }

    @Test
    void shouldReturnReplacementOrderWhenPaymentFails() throws Exception {
        PayOrderInfo info = new PayOrderInfo(1L, OrderStatus.PAYMENT_FAILED, false, PaymentResult.FAILED, 2L);
        when(orderService.pay(eq(1001L), eq(1L), any())).thenReturn(info);

        mockMvc.perform(post("/api/orders/1/pay")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "FAIL_CARD"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paid").value(false))
                .andExpect(jsonPath("$.data.paymentResult").value("FAILED"))
                .andExpect(jsonPath("$.data.replacementOrderId").value(2L));
    }

    @Test
    void shouldGetOrderDetail() throws Exception {
        OrderDetailInfo info = new OrderDetailInfo(
                1L,
                1001L,
                OrderStatus.PAID,
                new BigDecimal("31800"),
                new BigDecimal("3180"),
                new BigDecimal("28620"),
                List.of(new OrderItemInfo(11L, "레고 스타터 세트", new BigDecimal("15900"), 2, new BigDecimal("31800"))),
                new OrderDetailsSnapshotInfo(
                        "홍길동",
                        "01012345678",
                        "06236",
                        "서울특별시 강남구 테헤란로 123",
                        "101동 202호",
                        "WELCOME10",
                        PaymentMethod.CARD
                ),
                LocalDateTime.of(2026, 2, 7, 10, 15, 30)
        );

        when(orderService.getOrder(1001L, 1L)).thenReturn(info);

        mockMvc.perform(get("/api/orders/1")
                        .header("X-Member-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.status").value("PAID"))
                .andExpect(jsonPath("$.data.orderDetails.receiverName").value("홍길동"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        doThrow(new OrderNotFoundException(999L))
                .when(orderService).getOrder(1001L, 999L);

        mockMvc.perform(get("/api/orders/999")
                        .header("X-Member-Id", "1001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ORDER-404"));
    }

    @Test
    void shouldReturnBadRequestWhenReceiverPhoneIsInvalid() throws Exception {
        mockMvc.perform(post("/api/orders/1/details")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "receiverName": "홍길동",
                                  "receiverPhone": "abc",
                                  "zipCode": "06236",
                                  "addressLine1": "서울특별시 강남구 테헤란로 123",
                                  "addressLine2": "101동 202호",
                                  "couponCode": "WELCOME10",
                                  "paymentMethod": "CARD"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("receiverPhone은 숫자 9~11자리여야 합니다."));
    }
}
