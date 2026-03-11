package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.*;

public final class OrderDetails {

    private static final OrderDetails EMPTY = new OrderDetails(
            null,
            null,
            null,
            null,
            null,
            null,
            null
    );

    private final String receiverName;
    private final String receiverPhone;
    private final String zipCode;
    private final String addressLine1;
    private final String addressLine2;
    private final String couponCode;
    private final PaymentMethod paymentMethod;

    private OrderDetails(
            String receiverName,
            String receiverPhone,
            String zipCode,
            String addressLine1,
            String addressLine2,
            String couponCode,
            PaymentMethod paymentMethod
    ) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipCode = zipCode;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.couponCode = couponCode;
        this.paymentMethod = paymentMethod;
    }

    public static OrderDetails empty() {
        return EMPTY;
    }

    public static OrderDetails complete(
            String receiverName,
            String receiverPhone,
            String zipCode,
            String addressLine1,
            String addressLine2,
            String couponCode,
            PaymentMethod paymentMethod
    ) {
        if (paymentMethod == null) {
            throw new InvalidOrderPaymentMethodException(null);
        }

        return new OrderDetails(
                normalizeReceiverName(receiverName),
                normalizeReceiverPhone(receiverPhone),
                normalizeZipCode(zipCode),
                normalizeRequiredAddress("addressLine1", addressLine1),
                normalizeOptional(addressLine2),
                normalizeCouponCode(couponCode),
                paymentMethod
        );
    }

    public static OrderDetails restore(
            String receiverName,
            String receiverPhone,
            String zipCode,
            String addressLine1,
            String addressLine2,
            String couponCode,
            PaymentMethod paymentMethod
    ) {
        if (receiverName == null
                && receiverPhone == null
                && zipCode == null
                && addressLine1 == null
                && addressLine2 == null
                && couponCode == null
                && paymentMethod == null) {
            return empty();
        }

        return complete(
                receiverName,
                receiverPhone,
                zipCode,
                addressLine1,
                addressLine2,
                couponCode,
                paymentMethod
        );
    }

    private static String normalizeReceiverName(String receiverName) {
        String normalized = normalizeOptional(receiverName);
        if (normalized == null) {
            throw new InvalidOrderReceiverNameException(receiverName);
        }
        return normalized;
    }

    private static String normalizeReceiverPhone(String receiverPhone) {
        String normalized = normalizeOptional(receiverPhone);
        if (normalized == null || !normalized.matches("^\\d{9,11}$")) {
            throw new InvalidOrderReceiverPhoneException(receiverPhone);
        }
        return normalized;
    }

    private static String normalizeZipCode(String zipCode) {
        String normalized = normalizeOptional(zipCode);
        if (normalized == null || !normalized.matches("^\\d{5}$")) {
            throw new InvalidOrderZipCodeException(zipCode);
        }
        return normalized;
    }

    private static String normalizeRequiredAddress(String fieldName, String value) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            throw new InvalidOrderAddressException(fieldName, value);
        }
        return normalized;
    }

    private static String normalizeCouponCode(String couponCode) {
        String normalized = normalizeOptional(couponCode);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public boolean isCompleted() {
        return paymentMethod != null;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
