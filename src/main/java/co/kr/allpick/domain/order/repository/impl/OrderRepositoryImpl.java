package co.kr.allpick.domain.order.repository.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component("orderRepositoryHandler")
@RequiredArgsConstructor
public class OrderRepositoryImpl {

    private final OrderRepository orderRepository;

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public boolean existsByOrderNumber(String orderNumber) {
        return orderRepository.existsByOrderNumber(orderNumber);
    }
}