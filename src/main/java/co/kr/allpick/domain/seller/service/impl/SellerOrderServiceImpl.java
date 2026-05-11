package co.kr.allpick.domain.seller.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.order.repository.OrderItemRepository;
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

    @Override
    public List<SellerOrderResponseDto> getSellerOrders(Long memberId) {
        Seller seller = getApprovedSeller(memberId);
        logger.info("[SellerOrderServiceImpl] 판매자 주문 목록 조회 - sellerId: {}", seller.getSellerId());

        return orderItemRepository.findSellerOrderItems(seller.getSellerId())
                .stream()
                .map(SellerOrderResponseDto::from)
                .toList();
    }

    private Seller getApprovedSeller(Long memberId) {
        Seller seller = sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(ErrorCode.SELLER_NOT_APPROVED);
        }

        return seller;
    }
}
