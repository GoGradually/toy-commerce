package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.order.OrderService;
import me.gogradually.toycommerce.application.order.dto.*;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.exception.OrderNotFoundException;
import me.gogradually.toycommerce.domain.order.exception.PaymentFailedException;
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
                OrderStatus.PENDING_PAYMENT,
                new BigDecimal("31800"),
                List.of(new OrderItemInfo(11L, "레고 스타터 세트", new BigDecimal("15900"), 2, new BigDecimal("31800")))
        );
        when(orderService.checkout(1001L)).thenReturn(info);

        mockMvc.perform(post("/api/orders/checkout")
                        .header("X-Member-Id", "1001"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"));
    }

    @Test
    void shouldPayOrder() throws Exception {
        PayOrderInfo info = new PayOrderInfo(1L, OrderStatus.PAID, true, PaymentResult.SUCCESS);
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
                .andExpect(jsonPath("$.data.paymentResult").value("SUCCESS"));
    }

    @Test
    void shouldGetOrderDetail() throws Exception {
        OrderDetailInfo info = new OrderDetailInfo(
                1L,
                1001L,
                OrderStatus.PAID,
                new BigDecimal("31800"),
                List.of(new OrderItemInfo(11L, "레고 스타터 세트", new BigDecimal("15900"), 2, new BigDecimal("31800"))),
                LocalDateTime.of(2026, 2, 7, 10, 15, 30)
        );

        when(orderService.getOrder(1001L, 1L)).thenReturn(info);

        mockMvc.perform(get("/api/orders/1")
                        .header("X-Member-Id", "1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.status").value("PAID"));
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
    void shouldReturnBadRequestWhenPaymentFails() throws Exception {
        doThrow(new PaymentFailedException(1L))
                .when(orderService).pay(eq(1001L), eq(1L), any());

        mockMvc.perform(post("/api/orders/1/pay")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": "FAIL_CARD"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PAYMENT-400-FAILED"));
    }

    @Test
    void shouldReturnBadRequestWhenPaymentTokenIsBlank() throws Exception {
        mockMvc.perform(post("/api/orders/1/pay")
                        .header("X-Member-Id", "1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "paymentToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("paymentToken은 필수입니다."));
    }
}
