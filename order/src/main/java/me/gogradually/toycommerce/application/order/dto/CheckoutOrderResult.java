package me.gogradually.toycommerce.application.order.dto;

public record CheckoutOrderResult(
        CheckoutOrderInfo order,
        boolean created
) {

    public static CheckoutOrderResult created(CheckoutOrderInfo order) {
        return new CheckoutOrderResult(order, true);
    }

    public static CheckoutOrderResult reused(CheckoutOrderInfo order) {
        return new CheckoutOrderResult(order, false);
    }
}
