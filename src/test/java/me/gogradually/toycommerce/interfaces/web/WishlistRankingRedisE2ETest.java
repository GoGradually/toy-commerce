package me.gogradually.toycommerce.interfaces.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class WishlistRankingRedisE2ETest {

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureRedis(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);
    }

    @Test
    void shouldReflectWishlistMutationsInPopularRankingApi() throws Exception {
        Long firstProductId = createActiveProduct("레디스 랭킹 A");
        Long secondProductId = createActiveProduct("레디스 랭킹 B");

        addWishlist(101L, firstProductId);
        addWishlist(102L, firstProductId);
        addWishlist(103L, firstProductId);
        addWishlist(201L, secondProductId);
        removeWishlist(103L, firstProductId);

        mockMvc.perform(get("/api/rankings/wishlist/popular")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rankings.length()").value(2))
                .andExpect(jsonPath("$.data.rankings[0].rank").value(1))
                .andExpect(jsonPath("$.data.rankings[0].productId").value(firstProductId))
                .andExpect(jsonPath("$.data.rankings[0].wishlistCount").value(2))
                .andExpect(jsonPath("$.data.rankings[1].rank").value(2))
                .andExpect(jsonPath("$.data.rankings[1].productId").value(secondProductId))
                .andExpect(jsonPath("$.data.rankings[1].wishlistCount").value(1));
    }

    private Long createActiveProduct(String name) throws Exception {
        String requestBody = objectMapper.writeValueAsString(new CreateProductPayload(
                name,
                15900,
                100,
                "ACTIVE"
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

    private void addWishlist(Long memberId, Long productId) throws Exception {
        mockMvc.perform(post("/api/products/{productId}/wishlist", productId)
                        .header("X-Member-Id", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private void removeWishlist(Long memberId, Long productId) throws Exception {
        mockMvc.perform(delete("/api/products/{productId}/wishlist", productId)
                        .header("X-Member-Id", memberId))
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
}
