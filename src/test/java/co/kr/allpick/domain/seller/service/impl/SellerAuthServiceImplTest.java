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

    // ==================== 판매자 등록 ====================

    @Test
    @DisplayName("판매자 등록 성공")
    void signup_validRequest_success() {
        // given
        Long memberId = 1L;

        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "나이키 코리아", "1234567890", "홍길동", "국민은행", "12345678901234");

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
    @DisplayName("판매자 등록 실패 - 사업자등록번호 중복")
    void signup_duplicateBusinessNumber_throwException() {
        // given
        Long memberId = 1L;
        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "나이키 코리아", "1234567890", "홍길동", "국민은행", "12345678901234");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerAuthService.signup(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.DUPLICATE_BUSINESS_NUMBER.getMessage());
    }

    @Test
    @DisplayName("판매자 등록 실패 - 존재하지 않는 회원")
    void signup_memberNotFound_throwException() {
        // given
        Long memberId = 1L;
        SellerSignupRequestDto dto = new SellerSignupRequestDto(
                "나이키 코리아", "1234567890", "홍길동", "국민은행", "12345678901234");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.signup(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    // ==================== 판매자 정보 수정 ====================

    @Test
    @DisplayName("판매자 정보 수정 성공")
    void update_validRequest_success() {
        // given
        Long sellerId = 1L;
        Long memberId = 1L;
        SellerUpdateRequestDto dto = new SellerUpdateRequestDto(
                "아디다스 코리아", "김철수", "신한은행", "98765432101234");

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        Seller mockSeller = Seller.builder()
                .businessName("나이키 코리아")
                .representativeName("홍길동")
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
    @DisplayName("판매자 정보 수정 실패 - 존재하지 않는 판매자")
    void update_sellerNotFound_throwException() {
        // given
        Long sellerId = 999L;
        Long memberId = 1L;
        SellerUpdateRequestDto dto = new SellerUpdateRequestDto(
                "아디다스 코리아", "김철수", "신한은행", "98765432101234");

        given(sellerRepository.findById(sellerId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.update(sellerId, dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SELLER_NOT_FOUND.getMessage());
    }

    // ==================== 로그인 ====================

    @Test
    @DisplayName("판매자 로그인 성공")
    void login_validRequest_success() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        Seller mockSeller = Seller.builder()
                .businessName("나이키 코리아")
                .representativeName("홍길동")
                .businessNumber("1234567890")
                .status(SellerStatus.APPROVED)
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);
        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(mockMember.getId())).willReturn(Optional.of(mockSeller));
        given(jwtProvider.createToken(any(JwtUserInfoDto.class))).willReturn("mockToken");

        // when
        SellerLoginResponseDto result = sellerAuthService.login(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("mockToken");
    }

    @Test
    @DisplayName("판매자 로그인 실패 - 이메일 없음")
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
    @DisplayName("판매자 로그인 실패 - 비밀번호 틀림")
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
    @DisplayName("판매자 로그인 실패 - 판매자 권한 없음")
    void login_notSeller_throwException() {
        // given
        SellerLoginRequestDto dto = new SellerLoginRequestDto("test@test.com", "password123");

        Member mockMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .build();

        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(mockMember));
        given(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).willReturn(true);
        given(sellerRepository.findByMemberIdAndDeletedAtIsNull(mockMember.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerAuthService.login(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("판매자 로그인 실패 - 정지 회원")
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

    // ==================== 판매자 삭제 ====================

    @Test
    @DisplayName("판매자 삭제 성공")
    void deleteSeller_validRequest_success() {
        // given
        Long sellerId = 1L;
        Long memberId = 1L;

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        Seller mockSeller = Seller.builder()
                .businessName("나이키 코리아")
                .representativeName("홍길동")
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
    @DisplayName("판매자 삭제 실패 - 존재하지 않는 판매자")
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
