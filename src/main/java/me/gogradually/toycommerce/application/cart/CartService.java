package me.gogradually.toycommerce.application.cart;

import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.cart.command.AddCartItemCommand;
import me.gogradually.toycommerce.application.cart.command.UpdateCartItemQuantityCommand;
import me.gogradually.toycommerce.application.cart.dto.CartInfo;
import me.gogradually.toycommerce.application.cart.dto.CartItemInfo;
import me.gogradually.toycommerce.domain.cart.Cart;
import me.gogradually.toycommerce.domain.cart.CartItem;
import me.gogradually.toycommerce.domain.cart.CartRepository;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductRepository;
import me.gogradually.toycommerce.domain.product.ProductStatus;
import me.gogradually.toycommerce.domain.product.exception.InactiveCartProductException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartInfo getCartItems(Long memberId) {
        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));

        List<CartItemInfo> items = new ArrayList<>();
        BigDecimal cartTotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Optional<Product> activeProduct = productRepository.findByIdAndStatus(cartItem.getProductId(), ProductStatus.ACTIVE);
            if (activeProduct.isEmpty()) {
                continue;
            }

            Product product = activeProduct.get();
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            cartTotal = cartTotal.add(lineTotal);
            items.add(new CartItemInfo(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    lineTotal
            ));
        }

        return new CartInfo(items, cartTotal);
    }

    @Transactional
    public void addCartItem(Long memberId, AddCartItemCommand command) {
        ensureProductCanBeAdded(command.productId());

        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));
        cart.addItem(command.productId(), command.quantity());

        persistCart(memberId, cart.getItems());
    }

    @Transactional
    public void updateQuantity(Long memberId, Long productId, UpdateCartItemQuantityCommand command) {
        ensureProductCanBeAdded(productId);

        Cart cart = Cart.of(memberId, cartRepository.findByMemberId(memberId));
        cart.changeQuantity(productId, command.quantity());

        persistCart(memberId, cart.getItems());
    }

    @Transactional
    public void removeCartItem(Long memberId, Long productId) {
        Cart.empty(memberId).removeItem(productId);
        cartRepository.deleteByMemberIdAndProductId(memberId, productId);
    }

    @Transactional
    public void clearCart(Long memberId) {
        Cart.empty(memberId);
        cartRepository.deleteByMemberId(memberId);
    }

    private void persistCart(Long memberId, List<CartItem> items) {
        for (CartItem item : items) {
            if (!item.getMemberId().equals(memberId)) {
                continue;
            }
            cartRepository.save(item);
        }
    }

    private void ensureProductCanBeAdded(Long productId) {
        validateProductId(productId);

        if (productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE).isPresent()) {
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        throw new InactiveCartProductException(product.getId(), product.getStatus());
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new InvalidCartProductIdException(productId);
        }
    }
}
