package co.kr.allpick.domain.seller.service;

import java.util.List;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;

public interface SellerOrderService {

    List<SellerOrderResponseDto> getSellerOrders(Long memberId);

    void updateOrderStatus(Long memberId, Long orderId, Order.OrderStatus newStatus);
}
