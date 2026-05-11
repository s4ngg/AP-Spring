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
import co.kr.allpick.global.util.S3Uploader;
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

    @Mock
    S3Uploader s3Uploader;

    @InjectMocks
    InquiryServiceImpl inquiryService;

    @Test
    @DisplayName("문의 등록 성공")
    void 문의_등록_성공() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                10L, 5L, Inquiry.InquiryType.PRODUCT,
                "사이즈 문의드립니다.", "정 사이즈인지 궁금합니다.");

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .orderItemId(10L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("사이즈 문의드립니다.")
                .content("정 사이즈인지 궁금합니다.")
                .build();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsById(10L)).thenReturn(true);
        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(mockInquiry);

        // when
        InquiryResponseDto result = inquiryService.createInquiry(1L, request, null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("사이즈 문의드립니다.");
        assertThat(result.getStatus()).isEqualTo(Inquiry.InquiryStatus.PENDING);
        assertThat(result.getAnswers()).isEmpty();
    }

    @Test
    @DisplayName("문의 등록 실패 - 존재하지 않는 회원")
    void 문의_등록_실패_회원없음() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                null, 5L, Inquiry.InquiryType.PRODUCT,
                "사이즈 문의드립니다.", "정 사이즈인지 궁금합니다.");

        when(memberRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> inquiryService.createInquiry(999L, request, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("문의 등록 실패 - 존재하지 않는 주문 상품")
    void 문의_등록_실패_주문상품없음() {
        // given
        InquiryCreateRequestDto request = new InquiryCreateRequestDto(
                999L, 5L, Inquiry.InquiryType.PRODUCT,
                "사이즈 문의드립니다.", "정 사이즈인지 궁금합니다.");

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> inquiryService.createInquiry(1L, request, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("문의 상세 조회 성공")
    void 문의_상세_조회_성공() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .orderItemId(10L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.DELIVERY)
                .title("배송 관련 문의")
                .content("배송이 언제 오나요?")
                .build();

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(2L)
                .content("3~5일 이내 도착 예정입니다.")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.findByInquiryId(inquiryId)).thenReturn(List.of(mockAnswer));

        // when
        InquiryResponseDto result = inquiryService.getInquiryById(inquiryId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("배송 관련 문의");
        assertThat(result.getAnswers()).hasSize(1);
        assertThat(result.getAnswers().get(0).getContent()).isEqualTo("3~5일 이내 도착 예정입니다.");
    }

    @Test
    @DisplayName("문의 상세 조회 실패 - 문의 없음")
    void 문의_상세_조회_실패_문의없음() {
        // given
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.getInquiryById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("내 문의 목록 조회 성공")
    void 내_문의_목록_조회_성공() {
        // given
        Long memberId = 1L;

        Inquiry mockInquiry1 = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("상품 문의1")
                .content("내용1")
                .build();

        Inquiry mockInquiry2 = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PAYMENT)
                .title("결제 문의")
                .content("결제 관련 내용")
                .build();

        when(inquiryRepository.findByMemberIdAndDeletedAtIsNull(memberId))
                .thenReturn(List.of(mockInquiry1, mockInquiry2));
        when(inquiryAnswerRepository.findByInquiryId(any())).thenReturn(List.of());

        // when
        List<InquiryResponseDto> result = inquiryService.getMyInquiries(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("상품 문의1");
        assertThat(result.get(1).getInquiryType()).isEqualTo(Inquiry.InquiryType.PAYMENT);
    }

    @Test
    @DisplayName("전체 문의 목록 조회 성공")
    void 전체_문의_목록_조회_성공() {
        // given
        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.ETC)
                .title("기타 문의")
                .content("기타 내용")
                .build();

        when(inquiryRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(mockInquiry));
        when(inquiryAnswerRepository.findByInquiryId(any())).thenReturn(List.of());

        // when
        List<InquiryResponseDto> result = inquiryService.getAllInquiries();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInquiryType()).isEqualTo(Inquiry.InquiryType.ETC);
    }

    @Test
    @DisplayName("관리자 답변 등록 성공")
    void 관리자_답변_등록_성공() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("상품 문의")
                .content("문의 내용")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("정 사이즈 입니다.");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .adminId(2L)
                .content("정 사이즈 입니다.")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addAdminAnswer(inquiryId, request, 2L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("정 사이즈 입니다.");
        assertThat(result.getAdminId()).isEqualTo(2L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("관리자 답변 등록 실패 - 문의 없음")
    void 관리자_답변_등록_실패_문의없음() {
        // given
        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("답변 내용");

        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.addAdminAnswer(999L, request, 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("판매자 답변 등록 성공")
    void 판매자_답변_등록_성공() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Seller mockSeller = Seller.builder()
                .sellerId(10L)
                .build();

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(2L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("상품 문의")
                .content("문의 내용")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("판매자 답변입니다.");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .sellerId(10L)
                .content("판매자 답변입니다.")
                .build();

        when(sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addSellerAnswer(inquiryId, request, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("판매자 답변입니다.");
        assertThat(result.getSellerId()).isEqualTo(10L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("판매자 답변 등록 성공 - 상품 소유권 검증 포함")
    void 판매자_답변_등록_성공_소유권검증() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Seller mockSeller = Seller.builder().sellerId(10L).build();
        Product mockProduct = Product.builder().seller(mockSeller).build();

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(2L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("상품 문의")
                .content("문의 내용")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("판매자 답변입니다.");

        InquiryAnswer mockAnswer = InquiryAnswer.builder()
                .inquiryId(inquiryId)
                .sellerId(10L)
                .content("판매자 답변입니다.")
                .build();

        when(sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(productRepository.findById(5L)).thenReturn(Optional.of(mockProduct));
        when(inquiryAnswerRepository.save(any(InquiryAnswer.class))).thenReturn(mockAnswer);

        // when
        InquiryAnswerResponseDto result = inquiryService.addSellerAnswer(inquiryId, request, memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("판매자 답변입니다.");
        assertThat(result.getSellerId()).isEqualTo(10L);
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.PROCESSING);
    }

    @Test
    @DisplayName("판매자 답변 등록 실패 - 판매자 아님")
    void 판매자_답변_등록_실패_판매자아님() {
        // given
        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("답변 내용");

        when(sellerRepository.findByMemberIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.addSellerAnswer(1L, request, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("판매자 답변 등록 실패 - 다른 판매자 상품 문의")
    void 판매자_답변_등록_실패_소유권없음() {
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
                .title("상품 문의")
                .content("문의 내용")
                .build();

        InquiryAnswerRequestDto request = new InquiryAnswerRequestDto("답변 내용");

        when(sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)).thenReturn(Optional.of(mockSeller));
        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));
        when(productRepository.findById(5L)).thenReturn(Optional.of(mockProduct));

        // when & then
        assertThatThrownBy(() -> inquiryService.addSellerAnswer(inquiryId, request, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("문의 취소 성공")
    void 문의_취소_성공() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("취소할 문의")
                .content("내용")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when
        inquiryService.cancelInquiry(inquiryId, memberId);

        // then
        assertThat(mockInquiry.getStatus()).isEqualTo(Inquiry.InquiryStatus.CANCELLED);
    }

    @Test
    @DisplayName("문의 취소 실패 - 문의 없음")
    void 문의_취소_실패_문의없음() {
        // given
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("문의 취소 실패 - 권한 없음")
    void 문의_취소_실패_권한없음() {
        // given
        Long inquiryId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(1L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("문의 제목")
                .content("내용")
                .build();

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(inquiryId, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("문의 취소 실패 - 접수 대기 상태가 아님")
    void 문의_취소_실패_취소불가상태() {
        // given
        Long inquiryId = 1L;
        Long memberId = 1L;

        Inquiry mockInquiry = Inquiry.builder()
                .memberId(memberId)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("문의 제목")
                .content("내용")
                .build();
        mockInquiry.updateStatus(Inquiry.InquiryStatus.PROCESSING);

        when(inquiryRepository.findById(inquiryId)).thenReturn(Optional.of(mockInquiry));

        // when & then
        assertThatThrownBy(() -> inquiryService.cancelInquiry(inquiryId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_CANNOT_CANCEL.getMessage());
    }
}
