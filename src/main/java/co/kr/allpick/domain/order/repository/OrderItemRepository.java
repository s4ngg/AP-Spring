package co.kr.allpick.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.OrderItem;

@Component("orderItemRepositoryHandler")
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder_OrderId(Long orderId);
}