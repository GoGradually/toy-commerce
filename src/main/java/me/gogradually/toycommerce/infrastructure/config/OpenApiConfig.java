package me.gogradually.toycommerce.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI toyCommerceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Toy Commerce API")
                        .description("상품 조회/관리, 찜, 인기 랭킹 API 문서")
                        .version("v1")
                        .contact(new Contact()
                                .name("Toy Commerce Team")
                                .email("dev@toy-commerce.local")));
    }

    @Bean
    public GroupedOpenApi publicProductOpenApi() {
        return GroupedOpenApi.builder()
                .group("public-products")
                .pathsToMatch("/api/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminProductOpenApi() {
        return GroupedOpenApi.builder()
                .group("admin-products")
                .pathsToMatch("/api/admin/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi wishlistOpenApi() {
        return GroupedOpenApi.builder()
                .group("wishlist")
                .pathsToMatch("/api/products/*/wishlist", "/api/rankings/wishlist/**")
                .build();
    }
}
