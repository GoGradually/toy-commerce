package me.gogradually.toycommerce.infrastructure.repository.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.gogradually.toycommerce.domain.order.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_order_id", columnList = "order_id"),
                @Index(name = "idx_order_items_product_id", columnList = "product_id")
        }
)
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name_snapshot", nullable = false, length = 100)
    private String productNameSnapshot;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "line_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static OrderItemJpaEntity from(OrderItem orderItem) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.id = orderItem.getId();
        entity.productId = orderItem.getProductId();
        entity.productNameSnapshot = orderItem.getProductNameSnapshot();
        entity.unitPrice = orderItem.getUnitPrice();
        entity.quantity = orderItem.getQuantity();
        entity.lineTotal = orderItem.getLineTotal();
        entity.createdAt = orderItem.getCreatedAt();
        entity.updatedAt = orderItem.getUpdatedAt();
        return entity;
    }

    public OrderItem toDomain(Long orderId) {
        return OrderItem.restore(
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

    void assignOrder(OrderJpaEntity order) {
        this.order = order;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
