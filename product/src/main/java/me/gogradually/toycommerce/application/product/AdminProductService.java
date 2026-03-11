package me.gogradually.toycommerce.application.product;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.product.command.CreateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductStockCommand;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.port.OpenOrderCancellationPort;
import me.gogradually.toycommerce.application.product.port.OrderProductReferenceQueryPort;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.ActiveProductDeletionException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import me.gogradually.toycommerce.domain.product.exception.ProductReferencedByOrderException;
import me.gogradually.toycommerce.domain.product.exception.ProductStockRemainingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;
    private final OpenOrderCancellationPort openOrderCancellationPort;
    private final OrderProductReferenceQueryPort orderProductReferenceQueryPort;

    @Transactional
    public ProductDetailInfo createProduct(CreateProductCommand command) {
        Product product = Product.create(command.name(), command.price(), command.stock(), command.status());
        Product saved = productRepository.save(product);
        return ProductDetailInfo.from(saved);
    }

    @Transactional
    public ProductDetailInfo updateProduct(Long productId, UpdateProductCommand command) {
        Product product = getProductById(productId);
        product.update(command.name(), command.price(), command.status());

        Product saved = productRepository.save(product);
        return ProductDetailInfo.from(saved);
    }

    @Transactional
    public ProductDetailInfo updateStock(Long productId, UpdateProductStockCommand command) {
        Product product = getProductById(productId);
        product.changeStock(command.stock());

        Product saved = productRepository.save(product);
        return ProductDetailInfo.from(saved);
    }

    @Transactional
    public ProductDetailInfo forceSoldOut(Long productId) {
        Product product = getProductByIdForUpdate(productId);
        openOrderCancellationPort.cancelOpenOrdersContainingProduct(productId);
        product.forceSoldOut();
        Product saved = productRepository.save(product);
        return ProductDetailInfo.from(saved);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        ensureProductIsDeletable(product);
        productRepository.deleteById(product.getId());
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private Product getProductByIdForUpdate(Long productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void ensureProductIsDeletable(Product product) {
        if (product.getStatus() != ProductStatus.INACTIVE) {
            throw new ActiveProductDeletionException(product.getId());
        }
        if (product.getStock() > 0) {
            throw new ProductStockRemainingException(product.getId(), product.getStock());
        }
        if (orderProductReferenceQueryPort.existsAnyOrderItemByProductId(product.getId())) {
            throw new ProductReferencedByOrderException(product.getId());
        }
    }
}
