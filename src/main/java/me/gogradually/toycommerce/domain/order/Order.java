package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.EmptyCartException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderCouponException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderMemberIdException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private static final String SUPPORTED_COUPON_CODE = "WELCOME10";
    private static final BigDecimal SUPPORTED_COUPON_RATE = new BigDecimal("0.10");

    private final Long id;
    private final Long memberId;
    private final BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private OrderDetails orderDetails;
    private OrderStatus status;

    private Order(
            Long id,
            Long memberId,
            OrderStatus status,
            OrderDetails orderDetails,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            List<OrderItem> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateMemberId(memberId);
        validateStatus(status);
        validateItems(memberId, items);
        validateAmounts(status, orderDetails, originalAmount, discountAmount, totalAmount, items);

        this.id = id;
        this.memberId = memberId;
        this.status = status;
        this.orderDetails = orderDetails == null ? OrderDetails.empty() : orderDetails;
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order checkout(Long memberId, List<OrderItem> items) {
        List<OrderItem> normalizedItems = items == null ? List.of() : items;
        return new Order(
                null,
                memberId,
                OrderStatus.CREATED,
                OrderDetails.empty(),
                calculateOriginalAmount(normalizedItems),
                BigDecimal.ZERO,
                calculateOriginalAmount(normalizedItems),
                normalizedItems,
                null,
                null
        );
    }

    public static Order restore(
            Long id,
            Long memberId,
            OrderStatus status,
            OrderDetails orderDetails,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            List<OrderItem> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Order(
                id,
                memberId,
                status,
                orderDetails,
                originalAmount,
                discountAmount,
                totalAmount,
                items == null ? List.of() : items,
                createdAt,
                updatedAt
        );
    }

    private static BigDecimal calculateOriginalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void completeDetails(OrderDetails details) {
        currentState().completeDetails(this, details);
    }

    public boolean markPaid() {
        return currentState().markPaid(this);
    }

    public void markPaymentFailed() {
        currentState().markPaymentFailed(this);
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new InvalidOrderMemberIdException(memberId);
        }
    }

    private void validateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status must not be null.");
        }
    }

    private void validateItems(Long memberId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyCartException(memberId);
        }
    }

    private void validateAmounts(
            OrderStatus status,
            OrderDetails orderDetails,
            BigDecimal originalAmount,
            BigDecimal discountAmount,
            BigDecimal totalAmount,
            List<OrderItem> items
    ) {
        BigDecimal expectedOriginal = calculateOriginalAmount(items);
        if (originalAmount == null || expectedOriginal.compareTo(originalAmount) != 0) {
            throw new IllegalArgumentException("Order originalAmount must match order item totals.");
        }

        if (discountAmount == null || discountAmount.signum() < 0) {
            throw new IllegalArgumentException("Order discountAmount must be zero or positive.");
        }

        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new IllegalArgumentException("Order totalAmount must be zero or positive.");
        }

        BigDecimal expectedTotal = originalAmount.subtract(discountAmount);
        if (expectedTotal.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Order totalAmount must match discounted original amount.");
        }

        OrderDetails normalizedOrderDetails = orderDetails == null ? OrderDetails.empty() : orderDetails;
        if (status == OrderStatus.CREATED && normalizedOrderDetails.isCompleted()) {
            throw new IllegalArgumentException("Created order must not have completed order details.");
        }

        if ((status == OrderStatus.INFO_COMPLETED || status == OrderStatus.PAID || status == OrderStatus.PAYMENT_FAILED)
                && !normalizedOrderDetails.isCompleted()) {
            throw new IllegalArgumentException("Completed or paid order must have completed order details.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    void applyCompletedDetails(OrderDetails details) {
        BigDecimal calculatedDiscountAmount = calculateDiscountAmount(details.getCouponCode());
        this.orderDetails = details;
        this.discountAmount = calculatedDiscountAmount;
        this.totalAmount = originalAmount.subtract(calculatedDiscountAmount);
        transitionTo(OrderStatus.INFO_COMPLETED);
    }

    void transitionTo(OrderStatus targetStatus) {
        this.status = targetStatus;
    }

    private OrderState currentState() {
        return OrderStates.from(status);
    }

    private BigDecimal calculateDiscountAmount(String couponCode) {
        if (couponCode == null) {
            return BigDecimal.ZERO;
        }

        if (!SUPPORTED_COUPON_CODE.equals(couponCode)) {
            throw new InvalidOrderCouponException(couponCode);
        }

        return originalAmount.multiply(SUPPORTED_COUPON_RATE)
                .setScale(0, RoundingMode.DOWN);
    }
}
