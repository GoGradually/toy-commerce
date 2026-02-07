package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.wishlist.WishlistService;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.interfaces.utils.GlobalExceptionHandler;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import me.gogradually.toycommerce.interfaces.utils.exception.ValidationErrorMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @Test
    void shouldAddWishlist() throws Exception {
        mockMvc.perform(post("/api/products/1/wishlist")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldRemoveWishlist() throws Exception {
        mockMvc.perform(delete("/api/products/1/wishlist")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        doThrow(new ProductNotFoundException(999L))
                .when(wishlistService).addWishlist(10L, 999L);

        mockMvc.perform(post("/api/products/999/wishlist")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-404"))
                .andExpect(jsonPath("$.error.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    void shouldReturnBadRequestWhenProductIsInactive() throws Exception {
        doThrow(new InactiveProductException(2L, ProductStatus.INACTIVE))
                .when(wishlistService).addWishlist(10L, 2L);

        mockMvc.perform(post("/api/products/2/wishlist")
                        .header("X-Member-Id", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-400-INACTIVE"))
                .andExpect(jsonPath("$.error.message").value("비활성 상품은 찜할 수 없습니다."));
    }
}
