package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.wishlist.WishlistService;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingInfo;
import me.gogradually.toycommerce.application.wishlist.dto.WishlistPopularRankingItemInfo;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.interfaces.utils.GlobalExceptionHandler;
import me.gogradually.toycommerce.interfaces.utils.exception.ToyCommerceExceptionErrorCodeMapper;
import me.gogradually.toycommerce.interfaces.utils.exception.ValidationErrorMessageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistRankingController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class WishlistRankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @Test
    void shouldGetPopularWishlistRankings() throws Exception {
        WishlistPopularRankingItemInfo item = new WishlistPopularRankingItemInfo(
                1,
                7L,
                "레고 클래식 세트",
                new BigDecimal("25900"),
                ProductStatus.ACTIVE,
                120L
        );
        when(wishlistService.getPopularRankings(10))
                .thenReturn(new WishlistPopularRankingInfo(List.of(item)));

        mockMvc.perform(get("/api/rankings/wishlist/popular")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.limit").value(10))
                .andExpect(jsonPath("$.data.rankings[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankings[0].productId").value(7L))
                .andExpect(jsonPath("$.data.rankings[0].wishlistCount").value(120));
    }

    @Test
    void shouldReturnBadRequestWhenLimitIsInvalid() throws Exception {
        mockMvc.perform(get("/api/rankings/wishlist/popular")
                        .param("limit", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"));
    }
}
