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
}