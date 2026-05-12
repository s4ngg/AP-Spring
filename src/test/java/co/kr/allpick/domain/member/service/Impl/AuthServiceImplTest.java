package co.kr.allpick.domain.member.service.Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.kr.allpick.domain.coupon.entity.Coupon;
import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.coupon.repository.CouponRepository;
import co.kr.allpick.domain.coupon.repository.MemberCouponRepository;
import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.impl.AuthServiceImpl;
import co.kr.allpick.domain.member.sms.service.SmsService;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtProvider jwtProvider;
    @Mock SellerRepository sellerRepository;
    @Mock SmsService smsService;
    @Mock CouponRepository couponRepository;
    @Mock MemberCouponRepository memberCouponRepository;

    @InjectMocks
    AuthServiceImpl authService;

    // ==================== 회원가입 ====================

    @Test
    @DisplayName("일반 회원가입 성공 - 웰컴 쿠폰 지급")
    void 일반_회원가입_성공_쿠폰지급() {
        SignupRequestDto dto = new SignupRequestDto(
                "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();

        when(smsService.isVerified(dto.getPhone())).thenReturn(true);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(couponRepository.findByCouponCode("WELCOME5000")).thenReturn(Optional.of(Coupon.builder().build()));

        authService.signup(dto);

        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberCouponRepository, times(1)).save(any(MemberCoupon.class));
    }

    @Test
    @DisplayName("일반 회원가입 성공 - 쿠폰 없어도 가입 성공")
    void 일반_회원가입_성공_쿠폰없음() {
        SignupRequestDto dto = new SignupRequestDto(
                "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();

        when(smsService.isVerified(dto.getPhone())).thenReturn(true);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(mockMember);
        when(couponRepository.findByCouponCode("WELCOME5000")).thenReturn(Optional.empty());

        authService.signup(dto);

        verify(memberRepository, times(1)).save(any(Member.class));
        verify(memberCouponRepository, times(0)).save(any(MemberCoupon.class));
    }

    @Test
    @DisplayName("일반 회원가입 실패 - SMS 미인증")
    void 일반_회원가입_실패_SMS미인증() {
        SignupRequestDto dto = new SignupRequestDto(
                "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        when(smsService.isVerified(dto.getPhone())).thenReturn(false);

        assertThatThrownBy(() -> authService.signup(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PHONE_NOT_VERIFIED.getMessage());
    }

    @Test
    @DisplayName("일반 회원가입 실패 - 중복 이메일")
    void 일반_회원가입_실패_중복이메일() {
        SignupRequestDto dto = new SignupRequestDto(
                "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        when(smsService.isVerified(dto.getPhone())).thenReturn(true);
        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    // ==================== 로그인 ====================

    @Test
    @DisplayName("로그인 성공 - 일반 회원 (isSeller = false)")
    void 로그인_성공_일반회원() {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(any(JwtUserInfoDto.class))).thenReturn("mockToken");
        given(sellerRepository.existsByMemberIdAndStatusAndDeletedAtIsNull(any(), any()))
                .willReturn(false);

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.isSeller()).isFalse();
    }

    @Test
    @DisplayName("로그인 성공 - 셀러 회원 (isSeller = true)")
    void 로그인_성공_셀러회원() {
        LoginRequestDto dto = new LoginRequestDto("seller@test.com", "password123");

        Member mockMember = Member.builder()
                .email("seller@test.com")
                .password("encodedPassword")
                .name("판매자")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(any(JwtUserInfoDto.class))).thenReturn("mockToken");
        given(sellerRepository.existsByMemberIdAndStatusAndDeletedAtIsNull(
                any(),
                eq(SellerStatus.APPROVED)
        )).willReturn(true);

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.isSeller()).isTrue();
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void 로그인_실패_이메일없음() {
        LoginRequestDto dto = new LoginRequestDto("none@test.com", "password123");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void 로그인_실패_비밀번호틀림() {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "wrongPassword");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("홍길동")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 정지 회원")
    void 로그인_실패_정지회원() {
        LoginRequestDto dto = new LoginRequestDto("blocked@test.com", "password123");

        Member mockMember = Member.builder()
                .email("blocked@test.com")
                .password("encodedPassword")
                .name("정지회원")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .status(0)
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_BLOCKED);
    }
}
