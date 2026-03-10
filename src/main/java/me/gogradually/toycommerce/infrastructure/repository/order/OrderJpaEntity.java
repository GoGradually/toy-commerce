package me.gogradually.toycommerce.infrastructure.repository.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.gogradually.toycommerce.domain.order.Order;
import me.gogradually.toycommerce.domain.order.OrderDetails;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import me.gogradually.toycommerce.domain.order.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_member_created_at", columnList = "member_id, created_at")
        }
)
public class OrderJpaEntity {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItemJpaEntity> items = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;
    @Column(name = "receiver_name", length = 50)
    private String receiverName;
    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;
    @Column(name = "zip_code", length = 10)
    private String zipCode;
    @Column(name = "address_line1", length = 255)
    private String addressLine1;
    @Column(name = "address_line2", length = 255)
    private String addressLine2;
    @Column(name = "coupon_code", length = 50)
    private String couponCode;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;
    @Column(name = "original_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal originalAmount;
    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountAmount;
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static OrderJpaEntity from(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.id = order.getId();
        entity.memberId = order.getMemberId();
        entity.status = order.getStatus();
        entity.receiverName = order.getOrderDetails().getReceiverName();
        entity.receiverPhone = order.getOrderDetails().getReceiverPhone();
        entity.zipCode = order.getOrderDetails().getZipCode();
        entity.addressLine1 = order.getOrderDetails().getAddressLine1();
        entity.addressLine2 = order.getOrderDetails().getAddressLine2();
        entity.couponCode = order.getOrderDetails().getCouponCode();
        entity.paymentMethod = order.getOrderDetails().getPaymentMethod();
        entity.originalAmount = order.getOriginalAmount();
        entity.discountAmount = order.getDiscountAmount();
        entity.totalAmount = order.getTotalAmount();
        entity.createdAt = order.getCreatedAt();
        entity.updatedAt = order.getUpdatedAt();

        for (me.gogradually.toycommerce.domain.order.OrderItem item : order.getItems()) {
            entity.addItem(OrderItemJpaEntity.from(item));
        }

        return entity;
    }

    public Order toDomain() {
        return Order.restore(
                id,
                memberId,
                status,
                OrderDetails.restore(
                        receiverName,
                        receiverPhone,
                        zipCode,
                        addressLine1,
                        addressLine2,
                        couponCode,
                        paymentMethod
                ),
                originalAmount,
                discountAmount,
                totalAmount,
                items.stream()
                        .map(item -> item.toDomain(id))
                        .toList(),
                createdAt,
                updatedAt
        );
    }

    private void addItem(OrderItemJpaEntity item) {
        item.assignOrder(this);
        this.items.add(item);
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
