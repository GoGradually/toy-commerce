package me.gogradually.toycommerce.application.product.port;

public interface OrderProductReferenceQueryPort {

    boolean existsAnyOrderItemByProductId(Long productId);
}
