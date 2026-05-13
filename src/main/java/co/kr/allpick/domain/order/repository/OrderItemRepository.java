package co.kr.allpick.domain.order.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.OrderItem;


@Component("orderItemRepositoryHandler")
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderId(Long orderId);
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product p JOIN FETCH p.seller s JOIN FETCH s.member WHERE oi.orderItemId = :id")
    Optional<OrderItem> findByIdWithSellerMember(@Param("id") Long id);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    BigDecimal sumTotalPriceByOrderId(@Param("orderId") Long orderId);
    
    // 상품주문과 연결된 member 객체 한번에 불러오기
    @Query( "SELECT oi FROM OrderItem oi " + 
    		"JOIN FETCH oi.order o " +
    		"JOIN FETCH o.member m " +
    		"WHERE oi.orderItemId = :orderItemId")
    Optional<OrderItem> findWithOrderAndMember(@Param("orderItemId") Long orderItemId);

    @Query("""
            SELECT oi
            FROM OrderItem oi
            JOIN FETCH oi.order o
            JOIN FETCH o.member
            JOIN FETCH oi.product
            WHERE oi.orderItemId IN :orderItemIds
            """)
    List<OrderItem> findAllWithOrderMemberAndProductByOrderItemIdIn(
            @Param("orderItemIds") List<Long> orderItemIds);

    @Query("""
            SELECT oi
            FROM OrderItem oi
            JOIN FETCH oi.order o
            JOIN FETCH o.member
            JOIN FETCH o.deliveryAddress
            JOIN FETCH oi.product p
            JOIN FETCH p.seller s
            WHERE s.sellerId = :sellerId
              AND o.status <> co.kr.allpick.domain.order.entity.Order.OrderStatus.PENDING
            ORDER BY o.orderedAt DESC
            """)
    List<OrderItem> findSellerOrderItems(@Param("sellerId") Long sellerId);

    @Query("""
            SELECT COUNT(oi) > 0 FROM OrderItem oi
            WHERE oi.order.orderId = :orderId
              AND oi.product.seller.sellerId = :sellerId
            """)
    boolean existsByOrderIdAndSellerId(@Param("orderId") Long orderId, @Param("sellerId") Long sellerId);

    @Query("""
            SELECT COUNT(DISTINCT s.sellerId)
            FROM OrderItem oi
            JOIN oi.product p
            JOIN p.seller s
            WHERE oi.order.orderId = :orderId
            """)
    long countDistinctSellersByOrderId(@Param("orderId") Long orderId);
}
