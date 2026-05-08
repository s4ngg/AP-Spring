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
    SellerRepository sellerRepository;  // ??ì¶”ê?

    @InjectMocks
    AuthServiceImpl authService;

    // ==================== ?¼ë°˜ ?Œì›ê°€??====================

    @Test
    @DisplayName("?¼ë°˜ ?Œì›ê°€???±ê³µ")
    void ?¼ë°˜_?Œì›ê°€???±ê³µ() {
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "?ê¸¸??, "010-1234-5678", "?œìš¸??ê°•ë‚¨êµ?);

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        authService.signup(dto);

        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("?¼ë°˜ ?Œì›ê°€???¤íŒ¨ - ì¤‘ë³µ ?´ë©”??)
    void ?¼ë°˜_?Œì›ê°€???¤íŒ¨_ì¤‘ë³µ?´ë©”??) {
        SignupRequestDto dto = new SignupRequestDto(
            "test@test.com", "password123", "?ê¸¸??, "010-1234-5678", "?œìš¸??ê°•ë‚¨êµ?);

        when(memberRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    // ==================== ë¡œê·¸??====================

    @Test
    @DisplayName("ë¡œê·¸???±ê³µ - ?¼ë°˜ ?Œì› (isSeller = false)")
    void ë¡œê·¸???±ê³µ_?¼ë°˜?Œì›() {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("?ê¸¸??)
                .phone("010-1234-5678")
                .address("?œìš¸??ê°•ë‚¨êµ?)
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(any(JwtUserInfoDto.class))).thenReturn("mockToken");
        when(sellerRepository.findByMember_Id(any())).thenReturn(Optional.empty());  // ???€???„ë‹˜

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("?ê¸¸??);
        assertThat(result.isSeller()).isFalse();  // ???€???„ë‹˜ ê²€ì¦?
    }

    @Test
    @DisplayName("ë¡œê·¸???±ê³µ - ?€???Œì› (isSeller = true)")
    void ë¡œê·¸???±ê³µ_?€?¬íšŒ??) {
        LoginRequestDto dto = new LoginRequestDto("seller@test.com", "password123");

        Member mockMember = Member.builder()
                .email("seller@test.com")
                .password("encodedPassword")
                .name("?ë§¤??)
                .phone("010-1234-5678")
                .address("?œìš¸??ê°•ë‚¨êµ?)
                .build();

        Seller mockSeller = Seller.builder()
                .member(mockMember)
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(any(JwtUserInfoDto.class))).thenReturn("mockToken");
        when(sellerRepository.findByMember_Id(any())).thenReturn(Optional.of(mockSeller));  // ???€?¬ìž„

        AuthResponseDto result = authService.login(dto);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.isSeller()).isTrue();  // ???€?¬ìž„ ê²€ì¦?
    }

    @Test
    @DisplayName("ë¡œê·¸???¤íŒ¨ - ?´ë©”???†ìŒ")
    void ë¡œê·¸???¤íŒ¨_?´ë©”?¼ì—†??) {
        LoginRequestDto dto = new LoginRequestDto("none@test.com", "password123");

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("ë¡œê·¸???¤íŒ¨ - ë¹„ë?ë²ˆí˜¸ ?€ë¦?)
    void ë¡œê·¸???¤íŒ¨_ë¹„ë?ë²ˆí˜¸?€ë¦?) {
        LoginRequestDto dto = new LoginRequestDto("test@test.com", "wrongPassword");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .name("?ê¸¸??)
                .phone("010-1234-5678")
                .address("?œìš¸??ê°•ë‚¨êµ?)
                .build();

        when(memberRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mockMember));
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("ë¡œê·¸???¤íŒ¨ - ?•ì? ?Œì›")
    void ë¡œê·¸???¤íŒ¨_?•ì??Œì›() {
        LoginRequestDto dto = new LoginRequestDto("blocked@test.com", "password123");

        Member mockMember = Member.builder()
                .email("blocked@test.com")
                .password("encodedPassword")
                .name("?•ì??Œì›")
                .phone("010-1234-5678")
                .address("?œìš¸??ê°•ë‚¨êµ?)
                .status(0)
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_BLOCKED);
    }
}
