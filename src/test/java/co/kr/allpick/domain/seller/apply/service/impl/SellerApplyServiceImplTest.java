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

    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€
    // ?½ìŠ¤ì²?ë©”ì„œ??
    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€

    private SellerApplyRequestDto createFullDto() {
        return SellerApplyRequestDto.builder()
                .businessName("?ŒìŠ¤?¸ìƒ??)
                .businessNumber("123-45-67890")
                .representativeName("?ê¸¸??)
                .bankName("êµ???€??)
                .bankAccount("123456789012")
                .build();
    }

    private SellerApplyRequestDto createDtoWithBusinessNumber(String businessNumber) {
        return SellerApplyRequestDto.builder()
                .businessNumber(businessNumber)
                .build();
    }

    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€
    // apply() ?ŒìŠ¤??
    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€

    @Test
    @DisplayName("?ë§¤??? ì²­ ?±ê³µ")
    void apply_success() {
        // given
        Long memberId = 1L;
        SellerApplyRequestDto dto = createFullDto();
        Member member = mock(Member.class);

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(sellerRepository.existsByMember_Id(memberId)).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        sellerApplyService.apply(dto, memberId);

        // then
        then(sellerRepository).should().save(any(Seller.class));
    }

    @Test
    @DisplayName("?¬ì—…?ë“±ë¡ë²ˆ??ì¤‘ë³µ ???ˆì™¸ ë°œìƒ")
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
    @DisplayName("?´ë? ?ë§¤??? ì²­???Œì›??ê²½ìš° ?ˆì™¸ ë°œìƒ")
    void apply_fail_sellerAlreadyExists() {
        // given
        Long memberId = 1L;
        SellerApplyRequestDto dto = createDtoWithBusinessNumber("123-45-67890");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(sellerRepository.existsByMember_Id(memberId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> sellerApplyService.apply(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_ALREADY_EXISTS);
    }

    @Test
    @DisplayName("?Œì›??ì¡´ìž¬?˜ì? ?Šì„ ê²½ìš° ?ˆì™¸ ë°œìƒ")
    void apply_fail_memberNotFound() {
        // given
        Long memberId = 99L;
        SellerApplyRequestDto dto = createDtoWithBusinessNumber("123-45-67890");

        given(sellerRepository.existsByBusinessNumber(dto.getBusinessNumber())).willReturn(false);
        given(sellerRepository.existsByMember_Id(memberId)).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerApplyService.apply(dto, memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€
    // getApplyStatus() ?ŒìŠ¤??
    // ?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€?€

    @Test
    @DisplayName("? ì²­ ?íƒœ ì¡°íšŒ ?±ê³µ")
    void getApplyStatus_success() {
        // given
        Long memberId = 1L;
        Seller seller = Seller.builder()
                .businessName("?ŒìŠ¤?¸ìƒ??)
                .businessNumber("123-45-67890")
                .status(SellerStatus.PENDING)
                .build();

        given(sellerRepository.findByMember_Id(memberId)).willReturn(Optional.of(seller));

        // when
        SellerApplyStatusResponseDto result = sellerApplyService.getApplyStatus(memberId);

        // then
        assertThat(result.getBusinessName()).isEqualTo("?ŒìŠ¤?¸ìƒ??);
        assertThat(result.getBusinessNumber()).isEqualTo("123-45-67890");
        assertThat(result.getStatus()).isEqualTo(SellerStatus.PENDING);
    }

    @Test
    @DisplayName("?ë§¤??? ì²­ ?´ì—­???†ì„ ê²½ìš° ?ˆì™¸ ë°œìƒ")
    void getApplyStatus_fail_sellerNotFound() {
        // given
        Long memberId = 1L;

        given(sellerRepository.findByMember_Id(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sellerApplyService.getApplyStatus(memberId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
    }
}