package co.kr.allpick.domain.seller.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.domain.seller.service.SellerOrderService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerOrderServiceImpl implements SellerOrderService {

    private static final Logger logger = LogManager.getLogger(SellerOrderServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<SellerOrderResponseDto> getSellerOrders(Long memberId) {
        Seller seller = getApprovedSeller(memberId);
        logger.info("[SellerOrderServiceImpl] 판매자 주문 목록 조회 - sellerId: {}", seller.getSellerId());

        return orderItemRepository.findSellerOrderItems(seller.getSellerId())
                .stream()
                .map(SellerOrderResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long memberId, Long orderId, Order.OrderStatus newStatus) {
        Seller seller = getApprovedSeller(memberId);

        // 권한 + 존재 여부를 DB 한 번에 검증 (enumeration 방지를 위해 동일 응답 사용)
        if (!orderItemRepository.existsByOrderIdAndSellerId(orderId, seller.getSellerId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        validateSingleSellerOrder(orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Order.OrderStatus before = order.getStatus();
        validateStatusTransition(before, newStatus);
        order.updateStatus(newStatus);

        logger.info("[SellerOrderServiceImpl] 주문 상태 변경 - sellerId: {}, orderId: {}, {} -> {}",
                seller.getSellerId(), orderId, before, newStatus);
    }

    private Seller getApprovedSeller(Long memberId) {
        Seller seller = sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(ErrorCode.SELLER_NOT_APPROVED);
        }

        return seller;
    }

    private void validateSingleSellerOrder(Long orderId) {
        if (orderItemRepository.countDistinctSellersByOrderId(orderId) > 1) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ORDER);
        }
    }

    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        if (next == Order.OrderStatus.SHIPPING) {
            if (current != Order.OrderStatus.PAID) {
                throw new BusinessException(ErrorCode.ORDER_CANNOT_SHIP);
            }
            return;
        }
        if (next == Order.OrderStatus.DELIVERED) {
            if (current != Order.OrderStatus.SHIPPING) {
                throw new BusinessException(ErrorCode.ORDER_CANNOT_DELIVER);
            }
            return;
        }
        // SHIPPING, DELIVERED 외 상태로의 전이는 판매자가 트리거할 수 없음
        throw new BusinessException(ErrorCode.INVALID_INPUT);
    }
}
