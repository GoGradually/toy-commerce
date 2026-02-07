package me.gogradually.toycommerce.domain.order;

import me.gogradually.toycommerce.domain.order.exception.EmptyCartException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderMemberIdException;
import me.gogradually.toycommerce.domain.order.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private final Long id;
    private final Long memberId;
    private final BigDecimal totalAmount;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private OrderStatus status;

    private Order(
            Long id,
            Long memberId,
            OrderStatus status,
            BigDecimal totalAmount,
            List<OrderItem> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateMemberId(memberId);
        validateStatus(status);
        validateItems(memberId, items);
        validateTotalAmount(totalAmount, items);

        this.id = id;
        this.memberId = memberId;
        this.status = status;
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
                OrderStatus.PENDING_PAYMENT,
                calculateTotalAmount(normalizedItems),
                normalizedItems,
                null,
                null
        );
    }

    public static Order restore(
            Long id,
            Long memberId,
            OrderStatus status,
            BigDecimal totalAmount,
            List<OrderItem> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Order(
                id,
                memberId,
                status,
                totalAmount,
                items == null ? List.of() : items,
                createdAt,
                updatedAt
        );
    }

    private static BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean markPaid() {
        if (status == OrderStatus.PAID) {
            return false;
        }

        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStateException(status, OrderStatus.PENDING_PAYMENT, OrderStatus.PAID);
        }

        status = OrderStatus.PAID;
        return true;
    }

    public void markPaymentFailed() {
        if (status != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStateException(status, OrderStatus.PENDING_PAYMENT, OrderStatus.PAYMENT_FAILED);
        }

        status = OrderStatus.PAYMENT_FAILED;
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

    private void validateTotalAmount(BigDecimal totalAmount, List<OrderItem> items) {
        if (totalAmount == null || totalAmount.signum() < 0) {
            throw new IllegalArgumentException("Order totalAmount must be zero or positive.");
        }

        BigDecimal expectedTotal = calculateTotalAmount(items);
        if (expectedTotal.compareTo(totalAmount) != 0) {
            throw new IllegalArgumentException("Order totalAmount must match order item totals.");
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
}
