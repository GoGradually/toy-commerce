package me.gogradually.toycommerce.interfaces.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import me.gogradually.toycommerce.application.product.AdminProductService;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
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

@WebMvcTest(AdminProductController.class)
@Import({
        GlobalExceptionHandler.class,
        ToyCommerceExceptionErrorCodeMapper.class,
        ValidationErrorMessageResolver.class
})
class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminProductService adminProductService;

    @Test
    void shouldCreateProduct() throws Exception {
        ProductDetailInfo product = new ProductDetailInfo(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE
        );

        when(adminProductService.createProduct(any())).thenReturn(product);

        String request = objectMapper.writeValueAsString(new CreateProductPayload(
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE
        ));

        mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ProductDetailInfo product = new ProductDetailInfo(
                1L,
                "레고 프로 세트",
                new BigDecimal("25900"),
                40,
                ProductStatus.INACTIVE
        );

        when(adminProductService.updateProduct(any(), any())).thenReturn(product);

        String request = objectMapper.writeValueAsString(new UpdateProductPayload(
                "레고 프로 세트",
                new BigDecimal("25900"),
                ProductStatus.INACTIVE
        ));

        mockMvc.perform(patch("/api/admin/products/1")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("레고 프로 세트"));
    }

    @Test
    void shouldUpdateStock() throws Exception {
        ProductDetailInfo product = new ProductDetailInfo(
                1L,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                12,
                ProductStatus.ACTIVE
        );

        when(adminProductService.updateStock(any(), any())).thenReturn(product);

        String request = objectMapper.writeValueAsString(new UpdateStockPayload(12));

        mockMvc.perform(patch("/api/admin/products/1/stock")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stock").value(12));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/admin/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldReturnBadRequestWhenStockIsNegative() throws Exception {
        String request = objectMapper.writeValueAsString(new UpdateStockPayload(-1));

        mockMvc.perform(patch("/api/admin/products/1/stock")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownProduct() throws Exception {
        when(adminProductService.updateProduct(any(), any()))
                .thenThrow(new ProductNotFoundException(999L));

        String request = objectMapper.writeValueAsString(new UpdateProductPayload(
                "레고 프로 세트",
                new BigDecimal("25900"),
                ProductStatus.ACTIVE
        ));

        mockMvc.perform(patch("/api/admin/products/999")
                        .contentType(APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-404"))
                .andExpect(jsonPath("$.error.message").value("상품을 찾을 수 없습니다."));
    }

    private record CreateProductPayload(String name, BigDecimal price, Integer stock, ProductStatus status) {
    }

    private record UpdateProductPayload(String name, BigDecimal price, ProductStatus status) {
    }

    private record UpdateStockPayload(Integer stock) {
    }
}
