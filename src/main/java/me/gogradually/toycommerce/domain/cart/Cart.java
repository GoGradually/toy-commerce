package me.gogradually.toycommerce.domain.cart;

import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final Long memberId;
    private final List<CartItem> items;

    private Cart(Long memberId, List<CartItem> items) {
        validateMemberId(memberId);
        this.memberId = memberId;
        this.items = new ArrayList<>(items);
    }

    public static Cart empty(Long memberId) {
        return new Cart(memberId, List.of());
    }

    public static Cart of(Long memberId, List<CartItem> items) {
        return new Cart(memberId, items == null ? List.of() : items);
    }

    public void addItem(Long productId, int quantity) {
        validateProductId(productId);

        findItem(productId)
                .ifPresentOrElse(
                        item -> item.increaseQuantity(quantity),
                        () -> items.add(CartItem.create(memberId, productId, quantity))
                );
    }

    public void changeQuantity(Long productId, int quantity) {
        validateProductId(productId);

        findItem(productId)
                .ifPresentOrElse(
                        item -> item.changeQuantity(quantity),
                        () -> items.add(CartItem.create(memberId, productId, quantity))
                );
    }

    public void removeItem(Long productId) {
        validateProductId(productId);
        items.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    private java.util.Optional<CartItem> findItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();
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

    public Long getMemberId() {
        return memberId;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }
}
