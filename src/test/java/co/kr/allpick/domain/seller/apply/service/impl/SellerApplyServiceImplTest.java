package co.kr.allpick.domain.seller.apply.service.impl;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyRequestDto;
import co.kr.allpick.domain.seller.apply.dto.SellerApplyStatusResponseDto;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SellerApplyServiceImplTest {

    @InjectMocks
    private SellerApplyServiceImpl sellerApplyService;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private MemberRepository memberRepository;

    // ─────────────────────────────────────────
    // 픽스처 메서드
    // ─────────────────────────────────────────

    private SellerApplyRequestDto createFullDto() {
        return SellerApplyRequestDto.builder()
                .businessName("테스트상점")
                .businessNumber("123-45-67890")
                .representativeName("홍길동")
                .bankName("국민은행")
                .bankAccount("123456789012")
                .build();
    }

    private SellerApplyRequestDto createDtoWithBusinessNumber(String businessNumber) {
        return SellerApplyRequestDto.builder()
                .businessNumber(businessNumber)
                .build();
    }

    // ─────────────────────────────────────────
    // apply() 테스트
    // ─────────────────────────────────────────

    @Test
    @DisplayName("판매자 신청 성공")
    void apply_success() {
        // given
        Long memberId = 1L;
        SellerApplyRequestDto dto = createFullDto();
        Member member = mock(Member.class);

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(sellerRepository.existsByMemberId(memberId)).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        sellerApplyService.apply(dto, memberId);

        // then
        then(sellerRepository).should().save(any(Seller.class));
    }

    @Test
    @DisplayName("사업자등록번호 중복 시 예외 발생")
    void apply_fail_duplicateBusinessNumber() {
        // given
        Long memberId = 1L;
        SellerApplyRequestDto dto = createDtoWithBusinessNumber("123-45-67890");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerApplyService.apply(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_BUSINESS_NUMBER);
    }

    @Test
    @DisplayName("이미 판매자 신청한 회원일 경우 예외 발생")
    void apply_fail_sellerAlreadyExists() {
        // given
        Long memberId = 1L;
        SellerApplyRequestDto dto = createDtoWithBusinessNumber("123-45-67890");

        given(sellerRepository.existsByMemberId(memberId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerApplyService.apply(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("회원이 존재하지 않을 경우 예외 발생")
    void apply_fail_memberNotFound() {
        // given
        Long memberId = 99L;
        SellerApplyRequestDto dto = createDtoWithBusinessNumber("123-45-67890");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(sellerRepository.existsByMemberId(memberId)).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerApplyService.apply(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    // ─────────────────────────────────────────
    // getApplyStatus() 테스트
    // ─────────────────────────────────────────

    @Test
    @DisplayName("신청 상태 조회 성공")
    void getApplyStatus_success() {
        // given
        Long memberId = 1L;
        Seller seller = Seller.builder()
                .businessName("테스트상점")
                .businessNumber("123-45-67890")
                .status(SellerStatus.PENDING)
                .build();

        given(sellerRepository.findByMemberId(memberId)).willReturn(Optional.of(seller));

        // when
        SellerApplyStatusResponseDto result = sellerApplyService.getApplyStatus(memberId);

        // then
        assertThat(result.getBusinessName()).isEqualTo("테스트상점");
        assertThat(result.getBusinessNumber()).isEqualTo("123-45-67890");
        assertThat(result.getStatus()).isEqualTo(SellerStatus.PENDING);
    }

    @Test
    @DisplayName("판매자 신청 내역이 없을 경우 예외 발생")
    void getApplyStatus_fail_sellerNotFound() {
        // given
        Long memberId = 1L;

        given(sellerRepository.findByMemberId(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerApplyService.getApplyStatus(memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
    }
}
