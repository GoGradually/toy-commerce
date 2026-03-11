package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.cart.CartService;
import me.gogradually.toycommerce.application.cart.dto.CartInfo;
import me.gogradually.toycommerce.application.cart.dto.CartItemInfo;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void shouldGetCartItems() throws Exception {
        CartInfo info = new CartInfo(
                List.of(new CartItemInfo(1L, "레고 스타터 세트", new BigDecimal("15900"), 2, new BigDecimal("31800"))),
                new BigDecimal("31800")
        );
        when(cartService.getCartItems(10L)).thenReturn(info);

        mockMvc.perform(get("/api/cart/items")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items[0].productId").value(1L))
                .andExpect(jsonPath("$.data.cartTotal").value(31800));
    }

    @Test
    void shouldAddCartItem() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldUpdateCartItemQuantity() throws Exception {
        mockMvc.perform(patch("/api/cart/items/1")
                        .header("X-Member-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldRemoveCartItem() throws Exception {
        mockMvc.perform(delete("/api/cart/items/1")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldClearCart() throws Exception {
        mockMvc.perform(delete("/api/cart/items")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenQuantityIsInvalid() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("quantity는 1 이상이어야 합니다."));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        doThrow(new ProductNotFoundException(999L))
                .when(cartService).addCartItem(any(), any());

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 999,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-404"));
    }

    @Test
    void shouldReturnBadRequestWhenProductIsInactive() throws Exception {
        doThrow(new InactiveCartProductException(2L, ProductStatus.INACTIVE))
                .when(cartService).addCartItem(any(), any());

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 2,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CART-400-INACTIVE"))
                .andExpect(jsonPath("$.error.message").value("비활성 상품은 장바구니에 담을 수 없습니다."));
    }
}
