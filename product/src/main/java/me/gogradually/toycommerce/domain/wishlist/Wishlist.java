package me.gogradually.toycommerce.domain.wishlist;

import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistMemberIdException;
import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistProductIdException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Wishlist {

    private final Long id;
    private final Long memberId;
    private final Long productId;
    private final LocalDateTime createdAt;

    private Wishlist(Long id, Long memberId, Long productId, LocalDateTime createdAt) {
        validateMemberId(memberId);
        validateProductId(productId);

        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.createdAt = createdAt;
    }

    public static Wishlist create(Long memberId, Long productId) {
        return new Wishlist(null, memberId, productId, null);
    }

    public static Wishlist restore(Long id, Long memberId, Long productId, LocalDateTime createdAt) {
        return new Wishlist(id, memberId, productId, createdAt);
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new InvalidWishlistMemberIdException(memberId);
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidWishlistProductIdException(productId);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Wishlist wishlist)) {
            return false;
        }
        return Objects.equals(memberId, wishlist.memberId)
                && Objects.equals(productId, wishlist.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, productId);
    }
}
