package me.gogradually.toycommerce.interfaces.utils.exception;

import java.util.List;
import me.gogradually.toycommerce.application.product.exception.InvalidProductQueryException;
import me.gogradually.toycommerce.common.exception.ErrorCode;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductNameException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductPriceException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductStatusException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductStockException;
import me.gogradually.toycommerce.domain.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ToyCommerceExceptionErrorCodeMapper {

    private static final List<MappingRule> RULES = List.of(
            new MappingRule(ProductNotFoundException.class, ErrorCode.PRODUCT_NOT_FOUND),
            new MappingRule(InvalidProductNameException.class, ErrorCode.INVALID_PRODUCT_NAME),
            new MappingRule(InvalidProductPriceException.class, ErrorCode.INVALID_PRODUCT_PRICE),
            new MappingRule(InvalidProductStockException.class, ErrorCode.INVALID_PRODUCT_STOCK),
            new MappingRule(InvalidProductStatusException.class, ErrorCode.INVALID_REQUEST),
            new MappingRule(InvalidProductQueryException.class, ErrorCode.INVALID_REQUEST)
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
