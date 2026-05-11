package co.kr.allpick.domain.seller.service;

import java.util.List;

import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;

public interface SellerOrderService {

    List<SellerOrderResponseDto> getSellerOrders(Long memberId);
}
