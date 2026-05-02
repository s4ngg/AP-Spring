package co.kr.allpick.domain.seller.apply.service;

import co.kr.allpick.domain.seller.apply.dto.SellerApplyRequestDto;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyStatusResponseDto;

public interface SellerApplyService {
    void apply(SellerApplyRequestDto dto, Long memberId);
    SellerApplyStatusResponseDto getApplyStatus(Long memberId);
}