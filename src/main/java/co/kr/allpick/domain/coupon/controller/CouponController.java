package co.kr.allpick.domain.coupon.controller;

import co.kr.allpick.domain.coupon.controller.docs.CouponControllerDocs;
import co.kr.allpick.domain.coupon.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.coupon.dto.CouponResponseDto;
import co.kr.allpick.domain.coupon.dto.MemberCouponResponseDto;
import co.kr.allpick.domain.coupon.service.CouponService;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponController implements CouponControllerDocs {

    private final CouponService couponService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponseDto>> registerCoupon(
            @RequestBody @Valid CouponRegisterRequestDto request) {
        return ApiResponse.success("쿠폰이 등록되었습니다.", couponService.registerCoupon(request));
    }

    @Override
    @PostMapping("/{couponId}/members/{memberId}")
    public ResponseEntity<ApiResponse<MemberCouponResponseDto>> issueCoupon(
            @PathVariable("memberId") Long memberId,
            @PathVariable("couponId") Long couponId) {
        return ApiResponse.success("쿠폰이 발급되었습니다.", couponService.issueCoupon(memberId, couponId));
    }

    @Override
    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<List<MemberCouponResponseDto>>> getMemberCoupons(
            @PathVariable("memberId") Long memberId) {
        return ApiResponse.success("보유 쿠폰 목록 조회 성공.", couponService.getMemberCoupons(memberId));
    }

    @Override
    @GetMapping("/members/{memberId}/unused")
    public ResponseEntity<ApiResponse<List<MemberCouponResponseDto>>> getUnusedCoupons(
            @PathVariable("memberId") Long memberId) {
        return ApiResponse.success("미사용 쿠폰 목록 조회 성공.", couponService.getUnusedCoupons(memberId));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CouponResponseDto>> getCouponByCode(
            @RequestParam("couponCode") String couponCode) {
        return ApiResponse.success("쿠폰 조회 성공.", couponService.getCouponByCode(couponCode));
    }
}