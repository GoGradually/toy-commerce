package me.gogradually.toycommerce.infrastructure.repository.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.gogradually.toycommerce.domain.product.Product;
import me.gogradually.toycommerce.domain.product.ProductStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_products_status_created_at", columnList = "status, created_at"),
                @Index(name = "idx_products_name", columnList = "name")
        }
)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static ProductJpaEntity from(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.id = product.getId();
        entity.name = product.getName();
        entity.price = product.getPrice();
        entity.stock = product.getStock();
        entity.status = product.getStatus();
        entity.createdAt = product.getCreatedAt();
        entity.updatedAt = product.getUpdatedAt();
        return entity;
    }

    public Product toDomain() {
        return Product.restore(
                id,
                name,
                price,
                stock,
                status,
                createdAt,
                updatedAt
        );
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
