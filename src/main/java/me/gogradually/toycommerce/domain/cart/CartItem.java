package me.gogradually.toycommerce.domain.cart;

import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartQuantityException;

import java.time.LocalDateTime;

public class CartItem {

    private final Long id;
    private final Long memberId;
    private final Long productId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private int quantity;

    private CartItem(
            Long id,
            Long memberId,
            Long productId,
            int quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateMemberId(memberId);
        validateProductId(productId);
        validateQuantity(quantity);

        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CartItem create(Long memberId, Long productId, int quantity) {
        return new CartItem(null, memberId, productId, quantity, null, null);
    }

    public static CartItem restore(
            Long id,
            Long memberId,
            Long productId,
            int quantity,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new CartItem(id, memberId, productId, quantity, createdAt, updatedAt);
    }

    public void increaseQuantity(int quantityToAdd) {
        validateQuantity(quantityToAdd);
        this.quantity += quantityToAdd;
    }

    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null || memberId <= 0) {
            throw new InvalidCartMemberIdException(memberId);
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidCartProductIdException(productId);
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new InvalidCartQuantityException(quantity);
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

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
