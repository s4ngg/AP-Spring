package co.kr.allpick.domain.order.service;

import co.kr.allpick.domain.order.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.order.dto.CouponResponseDto;
import co.kr.allpick.domain.order.dto.MemberCouponResponseDto;

import java.util.List;

public interface CouponService {

    // 쿠폰 등록 (관리자)
    CouponResponseDto registerCoupon(CouponRegisterRequestDto request);

    // 쿠폰 발급 (회원에게)
    MemberCouponResponseDto issueCoupon(Long memberId, Long couponId);

    // 회원 보유 쿠폰 목록 조회
    List<MemberCouponResponseDto> getMemberCoupons(Long memberId);

    // 회원 미사용 쿠폰 목록 조회
    List<MemberCouponResponseDto> getUnusedCoupons(Long memberId);

    // 쿠폰 코드로 쿠폰 조회
    CouponResponseDto getCouponByCode(String couponCode);
}