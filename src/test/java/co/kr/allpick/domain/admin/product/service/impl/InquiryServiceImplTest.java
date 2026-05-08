package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import co.kr.allpick.domain.admin.product.entity.InquiryAnswer;
import co.kr.allpick.domain.admin.product.repository.AttachmentRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryAnswerRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryRepository;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InquiryServiceImplTest {

    @Mock
    InquiryRepository inquiryRepository;

    @Mock
    InquiryAnswerRepository inquiryAnswerRepository;

    @Mock
    AttachmentRepository attachmentRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    SellerRepository sellerRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    InquiryServiceImpl inquiryService;

    @Test
    @DisplayName("ë¬¸ì˜ ?±ë¡ ?±ê³µ")
    void ë¬¸ì˜_?±ë¡_?±ê³µ() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                10L, 5L, Inquiry.InquiryType.PRODUCT,
                "?¬ì´ì¦?ë¬¸ì˜?œë¦½?ˆë‹¤.", "???¬ì´ì¦ˆì¸ì§€ ê¶ê¸ˆ?©ë‹ˆ??");

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .orderItemId(10L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?¬ì´ì¦?ë¬¸ì˜?œë¦½?ˆë‹¤.")
                .content("???¬ì´ì¦ˆì¸ì§€ ê¶ê¸ˆ?©ë‹ˆ??")
                .build();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsById(10L)).thenReturn(true);
        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(mockInquiry);

        // when
        InquiryResponseDto result = inquiryService.createInquiry(1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("?¬ì´ì¦?ë¬¸ì˜?œë¦½?ˆë‹¤.");
        assertThat(result.getStatus()).isEqualTo(Inquiry.InquiryStatus.PENDING);
        assertThat(result.getAnswers()).isEmpty();
    }

    @Test
    @DisplayName("ë¬¸ì˜ ?±ë¡ ?¤íŒ¨ - ì¡´ì¬?˜ì? ?ŠëŠ” ?Œì›")
    void ë¬¸ì˜_?±ë¡_?¤íŒ¨_?Œì›?†ìŒ() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                null, 5L, Inquiry.InquiryType.PRODUCT,
                "?¬ì´ì¦?ë¬¸ì˜?œë¦½?ˆë‹¤.", "???¬ì´ì¦ˆì¸ì§€ ê¶ê¸ˆ?©ë‹ˆ??");

        when(memberRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> inquiryService.createInquiry(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("ë¬¸ì˜ ?±ë¡ ?¤íŒ¨ - ì¡´ì¬?˜ì? ?ŠëŠ” ì£¼ë¬¸ ?í’ˆ")
    void ë¬¸ì˜_?±ë¡_?¤íŒ¨_ì£¼ë¬¸?í’ˆ?†ìŒ() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                999L, 5L, Inquiry.InquiryType.PRODUCT,
                "?¬ì´ì¦?ë¬¸ì˜?œë¦½?ˆë‹¤.", "???¬ì´ì¦ˆì¸ì§€ ê¶ê¸ˆ?©ë‹ˆ??");

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> inquiryService.createInquiry(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("ë¬¸ì˜ ?ì„¸ ì¡°íšŒ ?±ê³µ")
    void ë¬¸ì˜_?ì„¸_ì¡°íšŒ_?±ê³µ() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .orderItemId(10L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.DELIVERY)
                .title("ë°°ì†¡ ê´€??ë¬¸ì˜")
                .content("ë°°ì†¡???¸ì œ ?¤ë‚˜??")
                .build();

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(2L)
                .content("3~5???´ë‚´ ?„ì°© ?ˆì •?…ë‹ˆ??")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.findByInquiryId(inquiryId)).thenReturn(List.of(mockAnswer));

        // when
        InquiryResponseDto result = inquiryService.getInquiryById(inquiryId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("ë°°ì†¡ ê´€??ë¬¸ì˜");
        assertThat(result.getAnswers()).hasSize(1);
        assertThat(result.getAnswers().get(0).getContent()).isEqualTo("3~5???´ë‚´ ?„ì°© ?ˆì •?…ë‹ˆ??");
    }

    @Test
    @DisplayName("ë¬¸ì˜ ?ì„¸ ì¡°íšŒ ?¤íŒ¨ - ë¬¸ì˜ ?†ìŒ")
    void ë¬¸ì˜_?ì„¸_ì¡°íšŒ_?¤íŒ¨_ë¬¸ì˜?†ìŒ() {
        // given
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.getInquiryById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("??ë¬¸ì˜ ëª©ë¡ ì¡°íšŒ ?±ê³µ")
    void ??ë¬¸ì˜_ëª©ë¡_ì¡°íšŒ_?±ê³µ() {
        // given
        Long memberId = 1L;

        Inquiry mockInquiry1 = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?í’ˆ ë¬¸ì˜1")
                .content("?´ìš©1")
                .build();

        Inquiry mockInquiry2 = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PAYMENT)
                .title("ê²°ì œ ë¬¸ì˜")
                .content("ê²°ì œ ê´€???´ìš©")
                .build();

        when(inquiryRepository.findByMemberIdAndDeletedAtIsNull(memberId))
                .thenReturn(List.of(mockInquiry1, mockInquiry2));

        // when
        List<InquiryResponseDto> result = inquiryService.getMyInquiries(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("?í’ˆ ë¬¸ì˜1");
        assertThat(result.get(1).getInquiryType()).isEqualTo(Inquiry.InquiryType.PAYMENT);
    }

    @Test
    @DisplayName("?„ì²´ ë¬¸ì˜ ëª©ë¡ ì¡°íšŒ ?±ê³µ")
    void ?„ì²´_ë¬¸ì˜_ëª©ë¡_ì¡°íšŒ_?±ê³µ() {
        // given
        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.ETC)
                .title("ê¸°í? ë¬¸ì˜")
                .content("ê¸°í? ?´ìš©")
                .build();

        when(inquiryRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(mockInquiry));

        // when
        List<InquiryResponseDto> result = inquiryService.getAllInquiries();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInquiryType()).isEqualTo(Inquiry.InquiryType.ETC);
    }

    @Test
    @DisplayName("ê´€ë¦¬ì ?µë? ?±ë¡ ?±ê³µ")
    void ê´€ë¦¬ì_?µë?_?±ë¡_?±ê³µ() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?í’ˆ ë¬¸ì˜")
                .content("ë¬¸ì˜ ?´ìš©")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("???¬ì´ì¦??…ë‹ˆ??");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(2L)
                .content("???¬ì´ì¦??…ë‹ˆ??")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addAdminAnswer(inquiryId, request, 2L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("???¬ì´ì¦??…ë‹ˆ??");
        assertThat(result.getAdminId()).isEqualTo(2L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("ê´€ë¦¬ì ?µë? ?±ë¡ ?¤íŒ¨ - ë¬¸ì˜ ?†ìŒ")
    void ê´€ë¦¬ì_?µë?_?±ë¡_?¤íŒ¨_ë¬¸ì˜?†ìŒ() {
        // given
        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("?µë? ?´ìš©");

        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.addAdminAnswer(999L, request, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("?ë§¤???µë? ?±ë¡ ?±ê³µ")
    void ?ë§¤???µë?_?±ë¡_?±ê³µ() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Seller mockSeller = Seller.builder()
                .sellerId(10L)
                .build();

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(2L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?í’ˆ ë¬¸ì˜")
                .content("ë¬¸ì˜ ?´ìš©")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("?ë§¤???µë??…ë‹ˆ??");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .sellerId(10L)
                .content("?ë§¤???µë??…ë‹ˆ??")
                .build();

        when(sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addSellerAnswer(inquiryId, request, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("?ë§¤???µë??…ë‹ˆ??");
        assertThat(result.getSellerId()).isEqualTo(10L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("?ë§¤???µë? ?±ë¡ ?±ê³µ - ?í’ˆ ?Œìœ ê¶?ê²€ì¦??¬í•¨")
    void ?ë§¤???µë?_?±ë¡_?±ê³µ_?Œìœ ê¶Œê?ì¦?) {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Seller mockSeller = Seller.builder().sellerId(10L).build();
        Product mockProduct = Product.builder().seller(mockSeller).build();

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(2L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?í’ˆ ë¬¸ì˜")
                .content("ë¬¸ì˜ ?´ìš©")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("?ë§¤???µë??…ë‹ˆ??");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .sellerId(10L)
                .content("?ë§¤???µë??…ë‹ˆ??")
                .build();

        when(sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(productRepository.findById(5L)).thenReturn(Optional.of(mockProduct));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addSellerAnswer(inquiryId, request, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("?ë§¤???µë??…ë‹ˆ??");
        assertThat(result.getSellerId()).isEqualTo(10L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("?ë§¤???µë? ?±ë¡ ?¤íŒ¨ - ?ë§¤???„ë‹˜")
    void ?ë§¤???µë?_?±ë¡_?¤íŒ¨_?ë§¤?ì•„??) {
        // given
        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("?µë? ?´ìš©");

        when(sellerRepository.findByMember_IdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.addSellerAnswer(1L, request, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("?ë§¤???µë? ?±ë¡ ?¤íŒ¨ - ?¤ë¥¸ ?ë§¤???í’ˆ ë¬¸ì˜")
    void ?ë§¤???µë?_?±ë¡_?¤íŒ¨_?Œìœ ê¶Œì—†??) {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Seller mockSeller = Seller.builder().sellerId(10L).build();
        Seller otherSeller = Seller.builder().sellerId(99L).build();
        Product mockProduct = Product.builder().seller(otherSeller).build();

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(2L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("?í’ˆ ë¬¸ì˜")
                .content("ë¬¸ì˜ ?´ìš©")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("?µë? ?´ìš©");

        when(sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(productRepository.findById(5L)).thenReturn(Optional.of(mockProduct));

        // when & then
        assertThatThrownBy(() -> inquiryService.addSellerAnswer(inquiryId, request, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("ë¬¸ì˜ ì·¨ì†Œ ?±ê³µ")
    void ë¬¸ì˜_ì·¨ì†Œ_?±ê³µ() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("ì·¨ì†Œ??ë¬¸ì˜")
                .content("?´ìš©")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when
        inquiryService.cancelInquiry(inquiryId, memberId);

        // then
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.CANCELLED);
    }

    @Test
    @DisplayName("ë¬¸ì˜ ì·¨ì†Œ ?¤íŒ¨ - ë¬¸ì˜ ?†ìŒ")
    void ë¬¸ì˜_ì·¨ì†Œ_?¤íŒ¨_ë¬¸ì˜?†ìŒ() {
        // given
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("ë¬¸ì˜ ì·¨ì†Œ ?¤íŒ¨ - ê¶Œí•œ ?†ìŒ")
    void ë¬¸ì˜_ì·¨ì†Œ_?¤íŒ¨_ê¶Œí•œ?†ìŒ() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("ë¬¸ì˜ ?œëª©")
                .content("?´ìš©")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(inquiryId, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("ë¬¸ì˜ ì·¨ì†Œ ?¤íŒ¨ - ?‘ìˆ˜ ?€ê¸??íƒœê°€ ?„ë‹˜")
    void ë¬¸ì˜_ì·¨ì†Œ_?¤íŒ¨_ì·¨ì†Œë¶ˆê??íƒœ() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("ë¬¸ì˜ ?œëª©")
                .content("?´ìš©")
                .build();
        mockInquiry.updateStatus(Inquiry.InquiryStatus.PROCESSING);

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(inquiryId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_CANNOT_CANCEL.getMessage());
    }
}
