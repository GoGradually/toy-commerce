package me.gogradually.toycommerce.infrastructure;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.OrderProductAdminService;
import me.gogradually.toycommerce.application.product.port.OpenOrderCancellationPort;
import me.gogradually.toycommerce.application.product.port.OrderProductReferenceQueryPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProductPortAdapter implements OpenOrderCancellationPort, OrderProductReferenceQueryPort {

    private final OrderProductAdminService orderProductAdminService;

    @Override
    public int cancelOpenOrdersContainingProduct(Long productId) {
        return orderProductAdminService.cancelOpenOrdersContainingProduct(productId);
    }

    @Override
    public boolean existsAnyOrderItemByProductId(Long productId) {
        return orderProductAdminService.existsAnyOrderItemByProductId(productId);
    }
}
