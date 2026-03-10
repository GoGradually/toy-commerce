package me.gogradually.toycommerce.domain.order;

final class CreatedOrderState implements OrderState {

    @Override
    public OrderStatus status() {
        return OrderStatus.CREATED;
    }

    @Override
    public void completeDetails(Order order, OrderDetails orderDetails) {
        order.applyCompletedDetails(orderDetails);
    }

    @Override
    public void cancel(Order order) {
        order.transitionTo(OrderStatus.CANCELLED);
    }
}
