package me.gogradually.toycommerce.interfaces.web;

import me.gogradually.toycommerce.application.product.ProductQueryService;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;
import me.gogradually.toycommerce.domain.product.ProductStatus;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductQueryService productQueryService;

    @Test
    void shouldGetProductList() throws Exception {
        ProductDetailInfo product = new ProductDetailInfo(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE
        );
        ProductPageInfo pageInfo = new ProductPageInfo(List.of(product), 0, 20, 1, 1, false);

        when(productQueryService.getProducts(0, 20, "createdAt", "desc"))
                .thenReturn(pageInfo);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.products[0].id").value(1L))
                .andExpect(jsonPath("$.data.products[0].name").value("레고 스타터 세트"));
    }

    @Test
    void shouldGetProductDetail() throws Exception {
        ProductDetailInfo product = new ProductDetailInfo(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE
        );

        when(productQueryService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(productQueryService.getProduct(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-404"))
                .andExpect(jsonPath("$.error.message").value("상품을 찾을 수 없습니다."));
    }
}
