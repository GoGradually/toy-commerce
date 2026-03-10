package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.InvalidOrderStateException;

interface OrderState {

    OrderStatus status();

    default void completeDetails(Order order, OrderDetails orderDetails) {
        throw new InvalidOrderStateException(order.getStatus(), OrderStatus.CREATED, OrderStatus.INFO_COMPLETED);
    }

    default boolean markPaid(Order order) {
        throw new InvalidOrderStateException(order.getStatus(), OrderStatus.INFO_COMPLETED, OrderStatus.PAID);
    }

    default void markPaymentFailed(Order order) {
        throw new InvalidOrderStateException(order.getStatus(), OrderStatus.INFO_COMPLETED, OrderStatus.PAYMENT_FAILED);
    }
}
