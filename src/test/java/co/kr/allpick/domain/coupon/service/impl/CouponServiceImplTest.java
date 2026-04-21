package co.kr.allpick.domain.coupon.service.impl;

import co.kr.allpick.domain.coupon.dto.CouponRegisterRequestDto;
import co.kr.allpick.domain.coupon.dto.CouponResponseDto;
import co.kr.allpick.domain.coupon.dto.MemberCouponResponseDto;
import co.kr.allpick.domain.coupon.entity.Coupon;
import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.CouponRepository;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.coupon.service.impl.CouponServiceImpl;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    CouponRepository couponRepository;

    @Mock
    MemberCouponRepository memberCouponRepository;

    @InjectMocks
    CouponServiceImpl couponService;

    @Test
    @DisplayName("쿠폰 등록 성공")
    void 쿠폰_등록_성공() {
        // given
        CouponRegisterRequestDto request = new CouponRegisterRequestDto(
                "WELCOME2026", Coupon.DiscountType.PERCENT,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10000),
                BigDecimal.valueOf(5000), LocalDateTime.now().plusMonths(1));

        Coupon mockCoupon = Coupon.builder()
                .couponCode("WELCOME2026")
                .discountType(Coupon.DiscountType.PERCENT)
                .discountValue(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(10000))
                .maxDiscount(BigDecimal.valueOf(5000))
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        when(couponRepository.existsByCouponCode("WELCOME2026")).thenReturn(false);
        when(couponRepository.save(any(Coupon.class))).thenReturn(mockCoupon);

        // when
        CouponResponseDto result = couponService.registerCoupon(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCouponCode()).isEqualTo("WELCOME2026");
        assertThat(result.getDiscountType()).isEqualTo(Coupon.DiscountType.PERCENT);
    }

    @Test
    @DisplayName("쿠폰 등록 실패 - 중복 코드")
    void 쿠폰_등록_실패_중복코드() {
        // given
        CouponRegisterRequestDto request = new CouponRegisterRequestDto(
                "WELCOME2026", Coupon.DiscountType.PERCENT,
                BigDecimal.valueOf(10), BigDecimal.valueOf(10000),
                BigDecimal.valueOf(5000), LocalDateTime.now().plusMonths(1));

        when(couponRepository.existsByCouponCode("WELCOME2026")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> couponService.registerCoupon(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COUPON_CODE_DUPLICATE.getMessage());
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void 쿠폰_발급_성공() {
        // given
        Long memberId = 1L;
        Long couponId = 1L;

        Coupon mockCoupon = Coupon.builder()
                .couponCode("WELCOME2026")
                .discountType(Coupon.DiscountType.PERCENT)
                .discountValue(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(10000))
                .maxDiscount(BigDecimal.valueOf(5000))
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        MemberCoupon mockMemberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .coupon(mockCoupon)
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));
        when(memberCouponRepository.save(any(MemberCoupon.class))).thenReturn(mockMemberCoupon);

        // when
        MemberCouponResponseDto result = couponService.issueCoupon(memberId, couponId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.isUsed()).isFalse();
    }

    @Test
    @DisplayName("쿠폰 발급 실패 - 쿠폰 없음")
    void 쿠폰_발급_실패_쿠폰없음() {
        // given
        when(couponRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회원 미사용 쿠폰 목록 조회 성공")
    void 회원_미사용_쿠폰_목록_조회_성공() {
        // given
        Long memberId = 1L;
        Coupon mockCoupon = Coupon.builder()
                .couponCode("WELCOME2026")
                .discountType(Coupon.DiscountType.PERCENT)
                .discountValue(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(10000))
                .maxDiscount(BigDecimal.valueOf(5000))
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        MemberCoupon mockMemberCoupon = MemberCoupon.builder()
                .memberId(memberId)
                .coupon(mockCoupon)
                .build();

        when(memberCouponRepository.findByMemberIdAndIsUsed(memberId, false))
                .thenReturn(List.of(mockMemberCoupon));

        // when
        List<MemberCouponResponseDto> result = couponService.getUnusedCoupons(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isUsed()).isFalse();
    }
    @Test
    @DisplayName("쿠폰 발급 실패 - 이미 보유한 쿠폰")
    void 쿠폰_발급_실패_이미보유() {
        // given
        Long memberId = 1L;
        Long couponId = 1L;

        Coupon mockCoupon = Coupon.builder()
                .couponCode("WELCOME2026")
                .discountType(Coupon.DiscountType.PERCENT)
                .discountValue(BigDecimal.valueOf(10))
                .minOrderAmount(BigDecimal.valueOf(10000))
                .maxDiscount(BigDecimal.valueOf(5000))
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(mockCoupon));
        when(memberCouponRepository.existsByMemberIdAndCoupon_CouponId(memberId, couponId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(memberId, couponId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COUPON_ALREADY_ISSUED.getMessage());
    }
}