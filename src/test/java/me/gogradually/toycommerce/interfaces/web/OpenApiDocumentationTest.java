package me.gogradually.toycommerce.interfaces.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldExposeOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.info.title").value("Toy Commerce API"));
    }

    @Test
    void shouldExposeGroupedOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/public-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products']").exists());
    }

    @Test
    void shouldExposeSwaggerUiPage() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assertTrue(statusCode >= 200 && statusCode < 400);
                });
    }

    @Test
    void shouldExposeWishlistGroupedOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/wishlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/products/{productId}/wishlist']").exists())
                .andExpect(jsonPath("$.paths['/api/rankings/wishlist/popular']").exists());
    }

    @Test
    void shouldExposeCartGroupedOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/cart/items']").exists())
                .andExpect(jsonPath("$.paths['/api/cart/items/{productId}']").exists());
    }

    @Test
    void shouldExposeOrderGroupedOpenApiJson() throws Exception {
        mockMvc.perform(get("/v3/api-docs/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/orders/checkout']").exists())
                .andExpect(jsonPath("$.paths['/api/orders/{orderId}']").exists())
                .andExpect(jsonPath("$.paths['/api/orders/{orderId}/pay']").exists());
    }
}
