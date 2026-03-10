package me.gogradually.toycommerce.domain.cart;

import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    @Test
    void shouldMergeQuantityWhenAddingSameProduct() {
        Cart cart = Cart.empty(1L);

        cart.addItem(100L, 2);
        cart.addItem(100L, 3);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldRemoveCartItem() {
        Cart cart = Cart.empty(1L);
        cart.addItem(100L, 2);

        cart.removeItem(100L);

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void shouldClearCart() {
        Cart cart = Cart.empty(1L);
        cart.addItem(100L, 2);
        cart.addItem(200L, 1);

        cart.clear();

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void shouldThrowWhenMemberIdIsInvalid() {
        assertThatThrownBy(() -> Cart.empty(0L))
                .isInstanceOf(InvalidCartMemberIdException.class);
    }

    @Test
    void shouldThrowWhenProductIdIsInvalid() {
        Cart cart = Cart.empty(1L);

        assertThatThrownBy(() -> cart.addItem(0L, 1))
                .isInstanceOf(InvalidCartProductIdException.class);
    }
}
