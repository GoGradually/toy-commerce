package me.gogradually.toycommerce.domain.wishlist;

import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistMemberIdException;
import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistProductIdException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WishlistTest {

    @Test
    void shouldPreventDuplicateByMemberIdAndProductId() {
        Wishlist first = Wishlist.create(1L, 100L);
        Wishlist duplicate = Wishlist.create(1L, 100L);

        Set<Wishlist> wishlists = new HashSet<>();
        wishlists.add(first);
        wishlists.add(duplicate);

        assertThat(wishlists).hasSize(1);
    }

    @Test
    void shouldThrowWhenMemberIdIsInvalid() {
        assertThatThrownBy(() -> Wishlist.create(0L, 100L))
                .isInstanceOf(InvalidWishlistMemberIdException.class);
    }

    @Test
    void shouldThrowWhenProductIdIsInvalid() {
        assertThatThrownBy(() -> Wishlist.create(1L, 0L))
                .isInstanceOf(InvalidWishlistProductIdException.class);
    }
}
