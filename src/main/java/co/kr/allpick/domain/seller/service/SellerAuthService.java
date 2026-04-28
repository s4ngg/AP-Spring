package co.kr.allpick.domain.seller.service;

import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;

public interface SellerAuthService {

    void signup(SellerSignupRequestDto dto, Long memberId);

    SellerLoginResponseDto login(SellerLoginRequestDto dto);
}