package co.kr.allpick.domain.seller.service;

import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;

public interface SellerAuthService {
    void signup(SellerSignupRequestDto dto, Long memberId);
    SellerLoginResponseDto login(SellerLoginRequestDto dto);
    void update(Long sellerId, SellerUpdateRequestDto dto, Long memberId);
    void deleteSeller(Long sellerId, Long memberId);
}