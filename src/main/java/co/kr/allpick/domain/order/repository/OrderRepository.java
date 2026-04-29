package co.kr.allpick.domain.order.repository;

import co.kr.allpick.domain.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"orderItems"})
    List<Order> findByMemberIdOrderByOrderedAtDesc(Long memberId);
    
    @Query("""
    	    SELECT o.memberId, SUM(o.totalAmount)
    	    FROM Order o
    	    WHERE o.orderedAt >= :start
    	      AND o.orderedAt < :end
    	      AND o.status = co.kr.allpick.domain.order.entity.Order.OrderStatus.DELIVERED
    	    GROUP BY o.memberId
    	    """)
    	List<Object[]> sumDeliveredAmountByMemberBetween(
    	        @Param("start") LocalDateTime start,
    	        @Param("end") LocalDateTime end
    	);
    
}