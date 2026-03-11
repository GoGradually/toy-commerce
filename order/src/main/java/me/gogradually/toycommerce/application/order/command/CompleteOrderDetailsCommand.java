package me.gogradually.toycommerce.application.order.command;

import me.gogradually.toycommerce.domain.order.OrderDetails;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

public record CompleteOrderDetailsCommand(
        String receiverName,
        String receiverPhone,
        String zipCode,
        String addressLine1,
        String addressLine2,
        String couponCode,
        PaymentMethod paymentMethod
) {

    public OrderDetails toOrderDetails() {
        return OrderDetails.complete(
                receiverName,
                receiverPhone,
                zipCode,
                addressLine1,
                addressLine2,
                couponCode,
                paymentMethod
        );
    }
}
