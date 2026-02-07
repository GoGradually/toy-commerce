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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartErrorReproE2ETest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReproduceCommon400WhenMemberIdHeaderIsInvalid() throws Exception {
        mockMvc.perform(get("/api/cart/items")
                        .header("X-Member-Id", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."));
    }

    @Test
    void shouldReproduceCommon400WhenProductIdInAddRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "1001")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 0,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("productId는 1 이상이어야 합니다."));
    }

    @Test
    void shouldReproduceCommon400WhenQuantityInAddRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "1002")
                        .contentType(APPLICATION_JSON)
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
    void shouldReproduceProduct404WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "1003")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 999999,
                                  "quantity": 1
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PRODUCT-404"));
    }

    @Test
    void shouldReproduceCart400InactiveWhenProductIsInactive() throws Exception {
        Long productId = createProduct("재현용 비활성 상품", 15900, 100, "ACTIVE");
        updateProduct(productId, "재현용 비활성 상품", 15900, "INACTIVE");

        mockMvc.perform(post("/api/cart/items")
                        .header("X-Member-Id", "1004")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddCartItemPayload(productId, 1))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("CART-400-INACTIVE"))
                .andExpect(jsonPath("$.error.message").value("비활성 상품은 장바구니에 담을 수 없습니다."));
    }

    @Test
    void shouldReproduceCommon400WhenProductIdPathVariableIsInvalid() throws Exception {
        mockMvc.perform(patch("/api/cart/items/{productId}", 0)
                        .header("X-Member-Id", "1005")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "quantity": 3
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON-400"))
                .andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."));
    }

    private Long createProduct(String name, int price, int stock, String status) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new CreateProductPayload(
                name,
                price,
                stock,
                status
        ));

        MvcResult result = mockMvc.perform(post("/api/admin/products")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        long productId = root.path("data").path("id").asLong();
        assertThat(productId).isPositive();
        return productId;
    }

    private void updateProduct(Long productId, String name, int price, String status) throws Exception {
        mockMvc.perform(patch("/api/admin/products/{productId}", productId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateProductPayload(name, price, status))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private record CreateProductPayload(
            String name,
            int price,
            int stock,
            String status
    ) {
    }

    private record UpdateProductPayload(
            String name,
            int price,
            String status
    ) {
    }

    private record AddCartItemPayload(
            Long productId,
            int quantity
    ) {
    }
}
