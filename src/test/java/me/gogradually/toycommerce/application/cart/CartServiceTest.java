package me.gogradually.toycommerce.application.cart;

import me.gogradually.toycommerce.application.cart.command.AddCartItemCommand;
import me.gogradually.toycommerce.application.cart.command.UpdateCartItemQuantityCommand;
import me.gogradually.toycommerce.application.cart.dto.CartInfo;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartQuantityException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldGetCartItemsWithTotalAndFilterInactiveProducts() {
        CartItem activeItem = CartItem.restore(1L, 10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());
        CartItem inactiveItem = CartItem.restore(2L, 10L, 200L, 1, LocalDateTime.now(), LocalDateTime.now());

        when(cartRepository.findByMemberId(10L)).thenReturn(List.of(activeItem, inactiveItem));
        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(activeProduct(100L)));
        when(productRepository.findByIdAndStatus(200L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());

        CartInfo result = cartService.getCartItems(10L);

        assertThat(result.items()).hasSize(1);
        assertThat(result.items().getFirst().productId()).isEqualTo(100L);
        assertThat(result.items().getFirst().lineTotal()).isEqualByComparingTo("31800");
        assertThat(result.cartTotal()).isEqualByComparingTo("31800");
    }

    @Test
    void shouldAddCartItem() {
        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(activeProduct(100L)));
        when(cartRepository.findByMemberId(10L)).thenReturn(List.of());

        cartService.addCartItem(10L, new AddCartItemCommand(100L, 2));

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(captor.capture());
        assertThat(captor.getValue().getMemberId()).isEqualTo(10L);
        assertThat(captor.getValue().getProductId()).isEqualTo(100L);
        assertThat(captor.getValue().getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldMergeQuantityWhenAddingSameProduct() {
        CartItem existing = CartItem.restore(1L, 10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());

        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(activeProduct(100L)));
        when(cartRepository.findByMemberId(10L)).thenReturn(List.of(existing));

        cartService.addCartItem(10L, new AddCartItemCommand(100L, 3));

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(1L);
        assertThat(captor.getValue().getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldUpdateQuantity() {
        CartItem existing = CartItem.restore(1L, 10L, 100L, 2, LocalDateTime.now(), LocalDateTime.now());

        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(activeProduct(100L)));
        when(cartRepository.findByMemberId(10L)).thenReturn(List.of(existing));

        cartService.updateQuantity(10L, 100L, new UpdateCartItemQuantityCommand(7));

        ArgumentCaptor<CartItem> captor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualTo(7);
    }

    @Test
    void shouldRemoveCartItem() {
        cartService.removeCartItem(10L, 100L);

        verify(cartRepository).deleteByMemberIdAndProductId(10L, 100L);
    }

    @Test
    void shouldClearCart() {
        cartService.clearCart(10L);

        verify(cartRepository).deleteByMemberId(10L);
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findByIdAndStatus(999L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addCartItem(10L, new AddCartItemCommand(999L, 1)))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldThrowWhenProductIsInactive() {
        when(productRepository.findByIdAndStatus(10L, ProductStatus.ACTIVE)).thenReturn(Optional.empty());
        when(productRepository.findById(10L)).thenReturn(Optional.of(inactiveProduct(10L)));

        assertThatThrownBy(() -> cartService.addCartItem(10L, new AddCartItemCommand(10L, 1)))
                .isInstanceOf(InactiveCartProductException.class);
    }

    @Test
    void shouldThrowWhenQuantityIsInvalid() {
        when(productRepository.findByIdAndStatus(100L, ProductStatus.ACTIVE)).thenReturn(Optional.of(activeProduct(100L)));
        when(cartRepository.findByMemberId(10L)).thenReturn(List.of());

        assertThatThrownBy(() -> cartService.addCartItem(10L, new AddCartItemCommand(100L, 0)))
                .isInstanceOf(InvalidCartQuantityException.class);
    }

    @Test
    void shouldThrowWhenProductIdIsInvalid() {
        assertThatThrownBy(() -> cartService.addCartItem(10L, new AddCartItemCommand(0L, 1)))
                .isInstanceOf(InvalidCartProductIdException.class);
    }

    @Test
    void shouldThrowWhenMemberIdIsInvalid() {
        assertThatThrownBy(() -> cartService.getCartItems(0L))
                .isInstanceOf(InvalidCartMemberIdException.class);
    }

    private Product activeProduct(Long id) {
        return Product.restore(
                id,
                "레고 스타터 세트",
                new BigDecimal("15900"),
                50,
                ProductStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(3)
        );
    }

    private Product inactiveProduct(Long id) {
        return Product.restore(
                id,
                "단종 세트",
                new BigDecimal("15900"),
                0,
                ProductStatus.INACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(5)
        );
    }
}
