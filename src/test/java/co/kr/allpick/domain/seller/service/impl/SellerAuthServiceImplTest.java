package co.kr.allpick.domain.seller.service.impl;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.dto.SellerLoginRequestDto;
import co.kr.allpick.domain.seller.dto.SellerLoginResponseDto;
import co.kr.allpick.domain.seller.dto.SellerSignupRequestDto;
import co.kr.allpick.domain.seller.dto.SellerUpdateRequestDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SellerAuthServiceImplTest {

    @Mock SellerRepository sellerRepository;
    @Mock MemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtProvider jwtProvider;

    @InjectMocks
    SellerAuthServiceImpl sellerAuthService;

    // ==================== ?ë§¤???±ë¡ ====================

    @Test
    @DisplayName("?ë§¤???±ë¡ ?±ê³µ")
    void signup_validRequest_success() {
        // given
        Long memberId = 1L;

        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "?˜ì´??ì½”ë¦¬??, "1234567890", "?ê¸¸??, "êµ???€??, "12345678901234");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .build();

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(mockMember));

        // when
        sellerAuthService.signup(dto, memberId);

        // then
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    @DisplayName("?ë§¤???±ë¡ ?¤íŒ¨ - ?¬ì—…?ë“±ë¡ë²ˆ??ì¤‘ë³µ")
    void signup_duplicateBusinessNumber_throwException() {
        // given
        Long memberId = 1L;
        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "?˜ì´??ì½”ë¦¬??, "1234567890", "?ê¸¸??, "êµ???€??, "12345678901234");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerAuthService.signup(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_BUSINESS_NUMBER.getMessage());
    }

    @Test
    @DisplayName("?ë§¤???±ë¡ ?¤íŒ¨ - ì¡´ìž¬?˜ì? ?ŠëŠ” ?Œì›")
    void signup_memberNotFound_throwException() {
        // given
        Long memberId = 1L;
        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "?˜ì´??ì½”ë¦¬??, "1234567890", "?ê¸¸??, "êµ???€??, "12345678901234");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.signup(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    // ==================== ?ë§¤???•ë³´ ?˜ì • ====================

    @Test
    @DisplayName("?ë§¤???•ë³´ ?˜ì • ?±ê³µ")
    void update_validRequest_success() {
        // given
        Long sellerId = 1L;
        Long memberId = 1L;
        SellerUpdateRequestDto dto = new SellerUpdateRequestDto(
                "?„ë””?¤ìŠ¤ ì½”ë¦¬??, "ê¹€ì² ìˆ˜", "? í•œ?€??, "98765432101234");

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        Seller mockSeller = Seller.builder()
                .businessName("?˜ì´??ì½”ë¦¬??)
                .representativeName("?ê¸¸??)
                .businessNumber("1234567890")
                .status(SellerStatus.APPROVED)
                .member(mockMember)
                .build();

        given(sellerRepository.findById(sellerId)).willReturn(Optional.of(mockSeller));

        // when
        sellerAuthService.update(sellerId, dto, memberId);

        // then
        verify(sellerRepository, times(1)).findById(sellerId);
    }

    @Test
    @DisplayName("?ë§¤???•ë³´ ?˜ì • ?¤íŒ¨ - ì¡´ìž¬?˜ì? ?ŠëŠ” ?ë§¤??)
    void update_sellerNotFound_throwException() {
        // given
        Long sellerId = 999L;
        Long memberId = 1L;
        SellerUpdateRequestDto dto = new SellerUpdateRequestDto(
                "?„ë””?¤ìŠ¤ ì½”ë¦¬??, "ê¹€ì² ìˆ˜", "? í•œ?€??, "98765432101234");

        given(sellerRepository.findById(sellerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.update(sellerId, dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SELLER_NOT_FOUND.getMessage());
    }

    // ==================== ë¡œê·¸??====================

    @Test
    @DisplayName("?ë§¤??ë¡œê·¸???±ê³µ")
    void login_validRequest_success() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        Seller mockSeller = Seller.builder()
                .businessName("?˜ì´??ì½”ë¦¬??)
                .representativeName("?ê¸¸??)
                .businessNumber("1234567890")
                .status(SellerStatus.APPROVED)
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);
        given(sellerRepository.findByMember_IdAndDeletedAtIsNull(mockMember.getId())).willReturn(Optional.of(mockSeller));
        given(jwtProvider.createToken(any(JwtUserInfoDto.class))).willReturn("mockToken");

        // when
        SellerLoginResponseDto result = sellerAuthService.login(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
    }

    @Test
    @DisplayName("?ë§¤??ë¡œê·¸???¤íŒ¨ - ?´ë©”???†ìŒ")
    void login_emailNotFound_throwException() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("none@test.com", "password123");

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("?ë§¤??ë¡œê·¸???¤íŒ¨ - ë¹„ë?ë²ˆí˜¸ ?€ë¦?)
    void login_invalidPassword_throwException() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("test@test.com", "wrongPassword");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> sellerAuthService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("?ë§¤??ë¡œê·¸???¤íŒ¨ - ?ë§¤??ê¶Œí•œ ?†ìŒ")
    void login_notSeller_throwException() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);
        given(sellerRepository.findByMember_IdAndDeletedAtIsNull(mockMember.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("?ë§¤??ë¡œê·¸???¤íŒ¨ - ?•ì? ?Œì›")
    void login_blockedMember_throwException() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("blocked@test.com", "password123");

        Member mockMember = Member.builder()
                .email("blocked@test.com")
                .password("encodedPassword")
                .status(0)
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerAuthService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_BLOCKED);
    }

    // ==================== ?ë§¤???? œ ====================

    @Test
    @DisplayName("?ë§¤???? œ ?±ê³µ")
    void deleteSeller_validRequest_success() {
        // given
        Long sellerId = 1L;
        Long memberId = 1L;

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        Seller mockSeller = Seller.builder()
                .businessName("?˜ì´??ì½”ë¦¬??)
                .representativeName("?ê¸¸??)
                .businessNumber("1234567890")
                .status(SellerStatus.APPROVED)
                .member(mockMember)
                .build();

        given(sellerRepository.findBySellerIdAndDeletedAtIsNull(sellerId))
                .willReturn(Optional.of(mockSeller));

        // when
        sellerAuthService.deleteSeller(sellerId, memberId);

        // then
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    @DisplayName("?ë§¤???? œ ?¤íŒ¨ - ì¡´ìž¬?˜ì? ?ŠëŠ” ?ë§¤??)
    void deleteSeller_sellerNotFound_throwException() {
        // given
        Long sellerId = 999L;
        Long memberId = 1L;

        given(sellerRepository.findBySellerIdAndDeletedAtIsNull(sellerId))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.deleteSeller(sellerId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SELLER_NOT_FOUND.getMessage());
    }
}
