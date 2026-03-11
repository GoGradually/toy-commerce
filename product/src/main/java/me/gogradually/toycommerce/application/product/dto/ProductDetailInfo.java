package me.gogradually.toycommerce.application.product.dto;

import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductStatus;

import java.math.BigDecimal;

public record ProductDetailInfo(
        Long id,
        String name,
        BigDecimal price,
        int stock,
        ProductStatus status
) {

    public static ProductDetailInfo from(Product product) {
        return new ProductDetailInfo(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus()
        );
    }
}
