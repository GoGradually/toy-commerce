package me.gogradually.toycommerce.domain.cart;

import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartQuantityException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartItemTest {

    @Test
    void shouldCreateCartItem() {
        CartItem cartItem = CartItem.create(1L, 100L, 2);

        assertThat(cartItem.getMemberId()).isEqualTo(1L);
        assertThat(cartItem.getProductId()).isEqualTo(100L);
        assertThat(cartItem.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldIncreaseQuantity() {
        CartItem cartItem = CartItem.create(1L, 100L, 2);

        cartItem.increaseQuantity(3);

        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldChangeQuantity() {
        CartItem cartItem = CartItem.create(1L, 100L, 2);

        cartItem.changeQuantity(7);

        assertThat(cartItem.getQuantity()).isEqualTo(7);
    }

    @Test
    void shouldThrowWhenMemberIdIsInvalid() {
        assertThatThrownBy(() -> CartItem.create(0L, 100L, 1))
                .isInstanceOf(InvalidCartMemberIdException.class);
    }

    @Test
    void shouldThrowWhenProductIdIsInvalid() {
        assertThatThrownBy(() -> CartItem.create(1L, 0L, 1))
                .isInstanceOf(InvalidCartProductIdException.class);
    }

    @Test
    void shouldThrowWhenQuantityIsInvalid() {
        assertThatThrownBy(() -> CartItem.create(1L, 100L, 0))
                .isInstanceOf(InvalidCartQuantityException.class);
    }
}
