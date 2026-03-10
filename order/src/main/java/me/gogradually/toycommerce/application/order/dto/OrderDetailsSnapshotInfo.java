package me.gogradually.toycommerce.application.order.dto;

import me.gogradually.toycommerce.domain.order.OrderDetails;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

public record OrderDetailsSnapshotInfo(
        String receiverName,
        String receiverPhone,
        String zipCode,
        String addressLine1,
        String addressLine2,
        String couponCode,
        PaymentMethod paymentMethod
) {

    public static OrderDetailsSnapshotInfo from(OrderDetails orderDetails) {
        return new OrderDetailsSnapshotInfo(
                orderDetails.getReceiverName(),
                orderDetails.getReceiverPhone(),
                orderDetails.getZipCode(),
                orderDetails.getAddressLine1(),
                orderDetails.getAddressLine2(),
                orderDetails.getCouponCode(),
                orderDetails.getPaymentMethod()
        );
    }
}
