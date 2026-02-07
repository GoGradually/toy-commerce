package me.gogradually.toycommerce.infrastructure.repository.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.gogradually.toycommerce.domain.cart.CartItem;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_items_member_product",
                        columnNames = {"member_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_cart_items_member_id", columnList = "member_id"),
                @Index(name = "idx_cart_items_product_id", columnList = "product_id")
        }
)
public class CartItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static CartItemJpaEntity from(CartItem cartItem) {
        CartItemJpaEntity entity = new CartItemJpaEntity();
        entity.id = cartItem.getId();
        entity.memberId = cartItem.getMemberId();
        entity.productId = cartItem.getProductId();
        entity.quantity = cartItem.getQuantity();
        entity.createdAt = cartItem.getCreatedAt();
        entity.updatedAt = cartItem.getUpdatedAt();
        return entity;
    }

    public CartItem toDomain() {
        return CartItem.restore(
                id,
                memberId,
                productId,
                quantity,
                createdAt,
                updatedAt
        );
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
