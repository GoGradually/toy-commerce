package me.gogradually.toycommerce.infrastructure.repository.order;

import jakarta.persistence.LockModeType;
import me.gogradually.toycommerce.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderJpaEntity o where o.id = :id")
    Optional<OrderJpaEntity> findByIdWithItems(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "items")
    @Query("select o from OrderJpaEntity o where o.id = :id")
    Optional<OrderJpaEntity> findByIdWithItemsForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findFirstByMemberIdAndStatusInOrderByUpdatedAtDescIdDesc(Long memberId, List<OrderStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "items")
    @Query("""
            select distinct o
            from OrderJpaEntity o
            where o.status in :statuses
              and o.updatedAt <= :updatedAt
            order by o.updatedAt asc
            """)
    List<OrderJpaEntity> findExpiredCancellationTargets(
            @Param("statuses") List<OrderStatus> statuses,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update OrderJpaEntity o
            set o.status = :status,
                o.updatedAt = :updatedAt
            where o.id in :orderIds
            """)
    int cancelExpiredOrders(
            @Param("orderIds") List<Long> orderIds,
            @Param("status") OrderStatus status,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "items")
    @Query("""
            select distinct o
            from OrderJpaEntity o
            join o.items i
            where o.status in :statuses
              and i.productId = :productId
            order by o.updatedAt asc, o.id asc
            """)
    List<OrderJpaEntity> findOpenOrdersContainingProduct(
            @Param("productId") Long productId,
            @Param("statuses") List<OrderStatus> statuses
    );

    @Query("""
            select count(i) > 0
            from OrderItemJpaEntity i
            where i.productId = :productId
            """)
    boolean existsAnyOrderItemByProductId(@Param("productId") Long productId);
}
