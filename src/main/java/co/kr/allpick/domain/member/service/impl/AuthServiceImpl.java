package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.coupon.entity.Coupon;
import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.CouponRepository;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.member.service.AuthService;
import co.kr.allpick.domain.member.sms.service.SmsService;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LogManager.getLogger(AuthServiceImpl.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SmsService smsService;
    private final SellerRepository sellerRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    @Override
    public void signup(SignupRequestDto dto) {
        if (!smsService.isVerified(dto.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        if (memberRepository.existsByEmail(dto.getEmail())) {
            logger.warn("[AuthService] 이메일 중복 - email: {}");
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        Member member = memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        smsService.removeVerified(dto.getPhone());

        couponRepository.findByCouponCode("WELCOME5000").ifPresent(coupon -> {
            MemberCoupon memberCoupon = MemberCoupon.builder()
                    .memberId(member.getId())
                    .coupon(coupon)
                    .build();
            memberCouponRepository.save(memberCoupon);
            logger.info("[AuthService] 웰컴 쿠폰 지급 완료 - memberId: {}", member.getId());
        });

        logger.info("[AuthService] 회원가입 완료");
    }

    @Override
    public String verifyAndFindId(String phoneNumber, String inputCode) {
        smsService.verifyCode(phoneNumber, inputCode);
        return memberRepository.findEmailByUserPhone(phoneNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_BY_PHONE));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    logger.warn("[AuthService] 존재하지 않는 이메일로 로그인 시도");
                    return new BusinessException(ErrorCode.INVALID_PASSWORD);
                });
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            logger.warn("[AuthService] 비밀번호 불일치 - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        if (!Integer.valueOf(1).equals(member.getStatus()) || member.getDeletedAt() != null) {
            logger.warn("[AuthService] 정지 회원 로그인 시도 - memberId: {}", member.getId());
            throw new BusinessException(ErrorCode.MEMBER_BLOCKED);
        }
        logger.info("[AuthService] 로그인 성공 - memberId: {}", member.getId());

        String token = jwtProvider.createToken(JwtUserInfoDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .build());

        boolean isSeller = sellerRepository.existsByMemberIdAndStatusAndDeletedAtIsNull(
                member.getId(),
                SellerStatus.APPROVED
        );

        return AuthResponseDto.of(token, member, isSeller);
    }
}
