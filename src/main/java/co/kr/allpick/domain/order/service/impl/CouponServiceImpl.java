package co.kr.allpick.domain.order.service.impl;

import co.kr.allpick.domain.order.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.order.dto.CouponResponseDto;
import co.kr.allpick.domain.order.dto.MemberCouponResponseDto;
import co.kr.allpick.domain.order.entity.Coupon;
import co.kr.allpick.domain.order.entity.MemberCoupon;
import co.kr.allpick.domain.order.repository.CouponRepository;
import co.kr.allpick.domain.order.repository.MemberCouponRepository;
import co.kr.allpick.domain.order.service.CouponService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final Logger logger = LogManager.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    @Override
    @Transactional
    public CouponResponseDto registerCoupon(CouponRegisterRequestDto request) {
        logger.info("쿠폰 등록 요청 - couponCode: {}", request.getCouponCode());

        if (couponRepository.existsByCouponCode(request.getCouponCode())) {
            throw new BusinessException(ErrorCode.COUPON_CODE_DUPLICATE);
        }

        Coupon saved = couponRepository.save(request.toEntity());
        logger.info("쿠폰 등록 완료 - couponId: {}", saved.getCouponId());
        return CouponResponseDto.from(saved);
    }

    @Override
    @Transactional
    public MemberCouponResponseDto issueCoupon(Long memberId, Long couponId) {
        logger.info("쿠폰 발급 요청 - memberId: {}, couponId: {}", memberId, couponId);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        // 중복 발급 체크
        if (memberCouponRepository.existsByMemberIdAndCoupon_CouponId(memberId, couponId)) {
            throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        MemberCoupon memberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .coupon(coupon)
                .build();

        MemberCoupon saved = memberCouponRepository.save(memberCoupon);
        logger.info("쿠폰 발급 완료 - memberCouponId: {}", saved.getMemberCouponId());
        return MemberCouponResponseDto.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponResponseDto> getMemberCoupons(Long memberId) {
        logger.info("회원 쿠폰 목록 조회 - memberId: {}", memberId);
        return memberCouponRepository.findByMemberId(memberId)
                .stream()
                .map(MemberCouponResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponResponseDto> getUnusedCoupons(Long memberId) {
        logger.info("회원 미사용 쿠폰 조회 - memberId: {}", memberId);
        return memberCouponRepository.findByMemberIdAndIsUsed(memberId, false)
                .stream()
                .map(MemberCouponResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCouponByCode(String couponCode) {
        logger.info("쿠폰 코드 조회 - couponCode: {}", couponCode);
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        return CouponResponseDto.from(coupon);
    }
}