package me.gogradually.toycommerce.interfaces.utils.exception;

import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartMemberIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartProductIdException;
import me.gogradually.toycommerce.domain.cart.exception.InvalidCartQuantityException;
import me.gogradually.toycommerce.domain.product.exception.*;
import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistMemberIdException;
import me.gogradually.toycommerce.domain.wishlist.exception.InvalidWishlistProductIdException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ToyCommerceExceptionErrorCodeMapper {

    private static final List<MappingRule> RULES = List.of(
            new MappingRule(ProductNotFoundException.class, ErrorCode.PRODUCT_NOT_FOUND),
            new MappingRule(InvalidProductNameException.class, ErrorCode.INVALID_PRODUCT_NAME),
            new MappingRule(InvalidProductPriceException.class, ErrorCode.INVALID_PRODUCT_PRICE),
            new MappingRule(InvalidProductStockException.class, ErrorCode.INVALID_PRODUCT_STOCK),
            new MappingRule(InactiveProductException.class, ErrorCode.INACTIVE_PRODUCT),
            new MappingRule(InactiveCartProductException.class, ErrorCode.INACTIVE_CART_PRODUCT),
            new MappingRule(InvalidProductStatusException.class, ErrorCode.INVALID_REQUEST),
            new MappingRule(InvalidProductQueryException.class, ErrorCode.INVALID_REQUEST),
            new MappingRule(InvalidWishlistMemberIdException.class, ErrorCode.INVALID_REQUEST),
            new MappingRule(InvalidWishlistProductIdException.class, ErrorCode.INVALID_REQUEST),
            new MappingRule(InvalidCartMemberIdException.class, ErrorCode.INVALID_CART_MEMBER),
            new MappingRule(InvalidCartProductIdException.class, ErrorCode.INVALID_CART_PRODUCT),
            new MappingRule(InvalidCartQuantityException.class, ErrorCode.INVALID_CART_QUANTITY)
    );

    public ErrorCode map(ToyCommerceException exception) {
        return RULES.stream()
                .filter(rule -> rule.type().isInstance(exception))
                .map(MappingRule::errorCode)
                .findFirst()
                .orElse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private record MappingRule(
            Class<? extends ToyCommerceException> type,
            ErrorCode errorCode
    ) {
    }
}
