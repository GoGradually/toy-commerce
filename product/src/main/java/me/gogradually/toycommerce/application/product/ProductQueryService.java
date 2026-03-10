package me.gogradually.toycommerce.application.product;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;
import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "price", "createdAt");
    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;

    public ProductPageInfo getProducts(int page, int size, String sortBy, String direction) {
        validatePage(page, size);

        String normalizedSortBy = normalizeSortBy(sortBy);
        Sort.Direction sortDirection = normalizeDirection(direction);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, normalizedSortBy));
        Page<ProductDetailInfo> mapped = productRepository.findByStatus(ProductStatus.ACTIVE, pageable)
                .map(ProductDetailInfo::from);

        return ProductPageInfo.from(mapped);
    }

    public ProductDetailInfo getProduct(Long productId) {
        Product product = productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        return ProductDetailInfo.from(product);
    }

    private void validatePage(int page, int size) {
        if (page < 0) {
            throw InvalidProductQueryException.invalidPage(page);
        }

        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw InvalidProductQueryException.invalidSize(size);
        }
    }

    private String normalizeSortBy(String sortBy) {
        String resolvedSortBy = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy;
        if (!ALLOWED_SORT_FIELDS.contains(resolvedSortBy)) {
            throw InvalidProductQueryException.invalidSortBy(resolvedSortBy);
        }
        return resolvedSortBy;
    }

    private Sort.Direction normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return Sort.Direction.DESC;
        }

        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw InvalidProductQueryException.invalidDirection(direction);
        }
    }
}
