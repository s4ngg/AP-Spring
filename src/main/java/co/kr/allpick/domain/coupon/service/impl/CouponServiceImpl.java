package co.kr.allpick.domain.coupon.service.impl;

import co.kr.allpick.domain.coupon.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.coupon.dto.CouponResponseDto;
import co.kr.allpick.domain.coupon.dto.MemberCouponResponseDto;
import co.kr.allpick.domain.coupon.entity.Coupon;
import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.CouponRepository;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.coupon.service.CouponService;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final Logger logger = LogManager.getLogger(CouponServiceImpl.class);

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final MemberRepository memberRepository; // #18, #22 회원 존재 검증용

    @Override
    @Transactional
    public CouponResponseDto registerCoupon(CouponRegisterRequestDto request) {
        logger.info("쿠폰 등록 요청 - couponCode: {}", request.getCouponCode());

        // #16 PERCENT 타입 100% 초과 검증
        if (request.getDiscountType() == Coupon.DiscountType.PERCENT
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

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

        // #18 회원 존재 검증
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        // #23 만료된 쿠폰 발급 방지
        if (coupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        // #17 중복 발급 체크
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

        // #22 회원 존재 검증
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return memberCouponRepository.findByMemberId(memberId)
                .stream()
                .map(MemberCouponResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberCouponResponseDto> getUnusedCoupons(Long memberId) {
        logger.info("회원 미사용 쿠폰 조회 - memberId: {}", memberId);

        // #22 회원 존재 검증
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

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

        if (coupon.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        return CouponResponseDto.from(coupon);
    }
}