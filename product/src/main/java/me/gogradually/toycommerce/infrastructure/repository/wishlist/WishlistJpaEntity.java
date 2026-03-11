package me.gogradually.toycommerce.infrastructure.repository.wishlist;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.gogradually.toycommerce.domain.wishlist.Wishlist;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "wishlists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wishlists_member_product",
                        columnNames = {"member_id", "product_id"}
                )
        },
        indexes = {
                @Index(name = "idx_wishlists_product_id", columnList = "product_id"),
                @Index(name = "idx_wishlists_member_id", columnList = "member_id")
        }
)
public class WishlistJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static WishlistJpaEntity from(Wishlist wishlist) {
        WishlistJpaEntity entity = new WishlistJpaEntity();
        entity.id = wishlist.getId();
        entity.memberId = wishlist.getMemberId();
        entity.productId = wishlist.getProductId();
        entity.createdAt = wishlist.getCreatedAt();
        return entity;
    }

    public Wishlist toDomain() {
        return Wishlist.restore(id, memberId, productId, createdAt);
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
