package me.gogradually.toycommerce.domain.product;

import me.gogradually.toycommerce.domain.product.exception.InvalidProductNameException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductPriceException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductStatusException;
import me.gogradually.toycommerce.domain.product.exception.InvalidProductStockException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {

    private final Long id;
    private String name;
    private BigDecimal price;
    private int stock;
    private ProductStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Product(
            Long id,
            String name,
            BigDecimal price,
            int stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        validateName(name);
        validatePrice(price);
        validateStock(stock);
        validateStatus(status);

        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product create(String name, BigDecimal price, int stock, ProductStatus status) {
        return new Product(null, name, price, stock, status, null, null);
    }

    public static Product restore(
            Long id,
            String name,
            BigDecimal price,
            int stock,
            ProductStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Product(id, name, price, stock, status, createdAt, updatedAt);
    }

    public void update(String name, BigDecimal price, ProductStatus status) {
        validateName(name);
        validatePrice(price);
        validateStatus(status);

        this.name = name.trim();
        this.price = price;
        this.status = status;
    }

    public void changeStock(int stock) {
        validateStock(stock);
        this.stock = stock;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductNameException(name);
        }
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new InvalidProductPriceException(price);
        }
    }

    private void validateStock(int stock) {
        if (stock < 0) {
            throw new InvalidProductStockException(stock);
        }
    }

    private void validateStatus(ProductStatus status) {
        if (status == null) {
            throw new InvalidProductStatusException(status);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
