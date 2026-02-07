package me.gogradually.toycommerce.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderItem {

    private final Long id;
    private final Long orderId;
    private final Long productId;
    private final String productNameSnapshot;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final BigDecimal lineTotal;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private OrderItem(
            Long id,
            Long orderId,
            Long productId,
            String productNameSnapshot,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal lineTotal,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateOrderId(orderId);
        validateProductId(productId);
        validateProductName(productNameSnapshot);
        validateUnitPrice(unitPrice);
        validateQuantity(quantity);

        BigDecimal expectedLineTotal = calculateLineTotal(unitPrice, quantity);
        if (lineTotal == null || expectedLineTotal.compareTo(lineTotal) != 0) {
            throw new IllegalArgumentException("Order item lineTotal must match unitPrice * quantity.");
        }

        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productNameSnapshot = productNameSnapshot.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderItem create(Long productId, String productNameSnapshot, BigDecimal unitPrice, int quantity) {
        return new OrderItem(
                null,
                null,
                productId,
                productNameSnapshot,
                unitPrice,
                quantity,
                calculateLineTotal(unitPrice, quantity),
                null,
                null
        );
    }

    public static OrderItem restore(
            Long id,
            Long orderId,
            Long productId,
            String productNameSnapshot,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal lineTotal,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new OrderItem(
                id,
                orderId,
                productId,
                productNameSnapshot,
                unitPrice,
                quantity,
                lineTotal,
                createdAt,
                updatedAt
        );
    }

    private static BigDecimal calculateLineTotal(BigDecimal unitPrice, int quantity) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Order item unitPrice must not be null.");
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void validateOrderId(Long orderId) {
        if (orderId != null && orderId <= 0) {
            throw new IllegalArgumentException("Order item orderId must be greater than 0.");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Order item productId must be greater than 0.");
        }
    }

    private void validateProductName(String productNameSnapshot) {
        if (productNameSnapshot == null || productNameSnapshot.trim().isEmpty()) {
            throw new IllegalArgumentException("Order item productNameSnapshot must not be blank.");
        }
    }

    private void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Order item unitPrice must be zero or positive.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be greater than 0.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
