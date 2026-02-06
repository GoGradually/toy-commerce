package me.gogradually.toycommerce.application.product;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ToyCommerceException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailInfo.from(product);
    }

    private void validatePage(int page, int size) {
        if (page < 0) {
            throw new ToyCommerceException(ErrorCode.INVALID_REQUEST, "page는 0 이상이어야 합니다.");
        }

        if (size <= 0 || size > MAX_PAGE_SIZE) {
            throw new ToyCommerceException(ErrorCode.INVALID_REQUEST, "size는 1 이상 100 이하이어야 합니다.");
        }
    }

    private String normalizeSortBy(String sortBy) {
        String resolvedSortBy = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy;
        if (!ALLOWED_SORT_FIELDS.contains(resolvedSortBy)) {
            throw new ToyCommerceException(
                    ErrorCode.INVALID_REQUEST,
                    "sortBy는 id, name, price, createdAt 중 하나여야 합니다."
            );
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
            throw new ToyCommerceException(ErrorCode.INVALID_REQUEST, "direction은 asc 또는 desc 이어야 합니다.");
        }
    }
}
