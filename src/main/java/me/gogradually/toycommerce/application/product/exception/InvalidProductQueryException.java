package me.gogradually.toycommerce.application.product.exception;

import java.util.Map;
import me.gogradually.toycommerce.common.exception.ToyCommerceException;

public class InvalidProductQueryException extends ToyCommerceException {

    private InvalidProductQueryException(String parameter, Object value, String reason) {
        super(
                "Invalid product query parameter.",
                Map.of(
                        "parameter", parameter,
                        "value", String.valueOf(value),
                        "reason", reason
                )
        );
    }

    public static InvalidProductQueryException invalidPage(int page) {
        return new InvalidProductQueryException("page", page, "must be greater than or equal to 0");
    }

    public static InvalidProductQueryException invalidSize(int size) {
        return new InvalidProductQueryException("size", size, "must be between 1 and 100");
    }

    public static InvalidProductQueryException invalidSortBy(String sortBy) {
        return new InvalidProductQueryException("sortBy", sortBy, "must be one of id,name,price,createdAt");
    }

    public static InvalidProductQueryException invalidDirection(String direction) {
        return new InvalidProductQueryException("direction", direction, "must be asc or desc");
    }
}
