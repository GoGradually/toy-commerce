package me.gogradually.toycommerce.application.product.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductPageInfo(
        List<ProductDetailInfo> products,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {

    public static ProductPageInfo from(Page<ProductDetailInfo> page) {
        return new ProductPageInfo(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
