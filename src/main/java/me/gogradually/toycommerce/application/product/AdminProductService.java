package me.gogradually.toycommerce.application.product;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.product.command.CreateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductCommand;
import me.gogradually.toycommerce.application.product.command.UpdateProductStockCommand;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductService {

    private final ProductRepository productRepository;

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
    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        productRepository.deleteById(product.getId());
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ToyCommerceException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
