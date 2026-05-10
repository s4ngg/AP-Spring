package co.kr.allpick.domain.member.service.Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import co.kr.allpick.domain.member.dto.AuthResponseDto;
import co.kr.allpick.domain.member.dto.LoginRequestDto;
import co.kr.allpick.domain.member.dto.SignupRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.impl.AuthServiceImpl;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    SellerRepository sellerRepository;  // ← 추가

    @InjectMocks
    AuthServiceImpl authService;

    // ==================== 일반 회원가입 ====================

    @Test
    @DisplayName("일반 회원가입 성공")
    void 일반_회원가입_성공() {
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        authService.signup(dto);

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("일반 회원가입 실패 - 중복 이메일")
    void 일반_회원가입_실패_중복이메일() {
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

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
        when(sellerRepository.findByMemberId(any())).thenReturn(Optional.empty());  // ← 셀러 아님

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.isSeller()).isFalse();  // ← 셀러 아님 검증
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

        Seller mockSeller = Seller.builder()
                .member(mockMember)
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(any(JwtUserInfoDto.class))).thenReturn("mockToken");
        when(sellerRepository.findByMemberId(any())).thenReturn(Optional.of(mockSeller));  // ← 셀러임

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.isSeller()).isTrue();  // ← 셀러임 검증
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
