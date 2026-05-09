package co.kr.allpick.domain.order.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.kr.allpick.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderNumber(String orderNumber);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.deliveryAddress.addressId = :addressId")
    boolean existsByAddressId(Long addressId);

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findByMemberIdOrderByOrderedAtDesc(Long memberId);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.member
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.product
            ORDER BY o.orderedAt DESC
            """)
    List<Order> findAllWithMemberAndItemsOrderByOrderedAtDesc();

    boolean existsByDeliveryAddress_AddressId(Long addressId);
 
    @Query("""
            SELECT o.member.id, SUM(o.totalAmount)
            FROM Order o
            WHERE o.orderedAt >= :start
              AND o.orderedAt < :end
              AND o.status = co.kr.allpick.domain.order.entity.Order.OrderStatus.DELIVERED
            GROUP BY o.member.id
            """)
    List<Object[]> sumDeliveredAmountByMemberBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
