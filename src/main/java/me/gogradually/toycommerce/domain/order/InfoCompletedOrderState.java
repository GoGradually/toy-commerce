package me.gogradually.toycommerce.domain.order;

final class InfoCompletedOrderState implements OrderState {

    @Override
    public OrderStatus status() {
        return OrderStatus.INFO_COMPLETED;
    }

    @Override
    public boolean markPaid(Order order) {
        order.transitionTo(OrderStatus.PAID);
        return true;
    }

    @Override
    public void markPaymentFailed(Order order) {
        order.transitionTo(OrderStatus.PAYMENT_FAILED);
    }
}
