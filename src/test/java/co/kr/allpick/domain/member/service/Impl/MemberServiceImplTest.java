package co.kr.allpick.domain.member.service.Impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import co.kr.allpick.domain.member.service.AuthServiceImpl;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.util.JwtProvider;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @InjectMocks
    AuthServiceImpl authService;

    // ==================== 회원가입 ====================

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() {
        // given
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        // when
        authService.signup(dto);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void 회원가입_실패_중복이메일() {
        // given
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "홍길동", "010-1234-5678", "서울시 강남구");

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    // ==================== 로그인 ====================

    @Test
    @DisplayName("로그인 성공")
    void 로그인_성공() {
        // given
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.createLocal(
            "test@test.com", "encodedPassword", "홍길동", "010-1234-5678", "서울시 강남구");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.generateToken(mockMember.getEmail())).thenReturn("mockToken");

        // when
        AuthResponseDto result = authService.login(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void 로그인_실패_이메일없음() {
        // given
        LoginRequestDto dto = new LoginRequestDto("none@test.com", "password123");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void 로그인_실패_비밀번호틀림() {
        // given
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "wrongPassword");

        Member mockMember = Member.createLocal(
            "test@test.com", "encodedPassword", "홍길동", "010-1234-5678", "서울시 강남구");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }
}