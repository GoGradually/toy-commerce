package me.gogradually.toycommerce.application.product.port;

public interface OpenOrderCancellationPort {

    int cancelOpenOrdersContainingProduct(Long productId);
}
