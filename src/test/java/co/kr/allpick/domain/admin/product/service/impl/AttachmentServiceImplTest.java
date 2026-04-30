package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.entity.Attachment;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import co.kr.allpick.domain.admin.product.repository.AttachmentRepository;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.util.S3Uploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    @Mock AttachmentRepository attachmentRepository;
    @Mock InquiryRepository inquiryRepository;
    @Mock ClaimRepository claimRepository;
    @Mock S3Uploader s3Uploader;

    @InjectMocks
    AttachmentServiceImpl attachmentService;

    private MockMultipartFile mockImageFile() {
        return new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
    }

    private Inquiry buildMockInquiry() {
        return Inquiry.builder()
                .memberId(1L)
                .orderItemId(10L)
                .productId(5L)
                .inquiryType(Inquiry.InquiryType.PRODUCT)
                .title("사이즈 문의")
                .content("정 사이즈인지 궁금합니다.")
                .build();
    }

    private Claim buildMockClaim() {
        return Claim.builder()
                .memberId(1L)
                .orderItemId(10L)
                .claimType(Claim.ClaimType.RETURN)
                .reasonCode(Claim.ReasonCode.CHANGE_MIND)
                .pickupMethod(Claim.ClaimPickupMethod.COURIER)
                .rejectReason(null)
                .build();
    }

    private Attachment buildMockAttachment(Attachment.TargetType type) {
        return Attachment.builder()
                .inquiryId(type == Attachment.TargetType.INQUIRY ? 1L : null)
                .claimId(type == Attachment.TargetType.CLAIM ? 1L : null)
                .targetType(type)
                .imageUrl("https://bucket.s3.amazonaws.com/test/uuid.jpg")
                .sortOrder(0)
                .build();
    }

    // ── 문의 첨부파일 업로드 ──────────────────────────────────────

    @Test
    @DisplayName("문의 첨부파일 업로드 성공")
    void 문의_첨부파일_업로드_성공() {
        // given
        Inquiry mockInquiry = buildMockInquiry();
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.INQUIRY);

        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(mockInquiry));
        when(s3Uploader.upload(any(), eq("inquiries"))).thenReturn("https://bucket.s3.amazonaws.com/inquiries/uuid.jpg");
        when(attachmentRepository.save(any())).thenReturn(mockAttachment);

        // when
        AttachmentResponseDto result = attachmentService.uploadInquiryAttachment(1L, mockImageFile(), 0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTargetType()).isEqualTo(Attachment.TargetType.INQUIRY);
    }

    @Test
    @DisplayName("문의 첨부파일 업로드 실패 - 존재하지 않는 문의")
    void 문의_첨부파일_업로드_실패_문의없음() {
        // given
        when(inquiryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> attachmentService.uploadInquiryAttachment(999L, mockImageFile(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("문의 첨부파일 업로드 실패 - 삭제된 문의")
    void 문의_첨부파일_업로드_실패_삭제된문의() {
        // given
        Inquiry deletedInquiry = buildMockInquiry();
        deletedInquiry.delete();

        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(deletedInquiry));

        // when & then
        assertThatThrownBy(() -> attachmentService.uploadInquiryAttachment(1L, mockImageFile(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_ALREADY_DELETED.getMessage());
    }

    // ── 클레임 첨부파일 업로드 ──────────────────────────────────────

    @Test
    @DisplayName("클레임 첨부파일 업로드 성공")
    void 클레임_첨부파일_업로드_성공() {
        // given
        Claim mockClaim = buildMockClaim();
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.CLAIM);

        when(claimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));
        when(s3Uploader.upload(any(), eq("claims"))).thenReturn("https://bucket.s3.amazonaws.com/claims/uuid.jpg");
        when(attachmentRepository.save(any())).thenReturn(mockAttachment);

        // when
        AttachmentResponseDto result = attachmentService.uploadClaimAttachment(1L, mockImageFile(), 0);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTargetType()).isEqualTo(Attachment.TargetType.CLAIM);
    }

    @Test
    @DisplayName("클레임 첨부파일 업로드 실패 - 존재하지 않는 클레임")
    void 클레임_첨부파일_업로드_실패_클레임없음() {
        // given
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> attachmentService.uploadClaimAttachment(999L, mockImageFile(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 첨부파일 업로드 실패 - 삭제된 클레임")
    void 클레임_첨부파일_업로드_실패_삭제된클레임() {
        // given
        Claim deletedClaim = buildMockClaim();
        deletedClaim.delete();

        when(claimRepository.findById(1L)).thenReturn(Optional.of(deletedClaim));

        // when & then
        assertThatThrownBy(() -> attachmentService.uploadClaimAttachment(1L, mockImageFile(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_DELETED.getMessage());
    }

    // ── 목록 조회 ──────────────────────────────────────

    @Test
    @DisplayName("문의 첨부파일 목록 조회 성공")
    void 문의_첨부파일_목록_조회_성공() {
        // given
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.INQUIRY);

        when(inquiryRepository.existsById(1L)).thenReturn(true);
        when(attachmentRepository.findByInquiryIdAndDeletedAtIsNull(1L)).thenReturn(List.of(mockAttachment));

        // when
        List<AttachmentResponseDto> result = attachmentService.getByInquiryId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetType()).isEqualTo(Attachment.TargetType.INQUIRY);
    }

    @Test
    @DisplayName("문의 첨부파일 목록 조회 실패 - 존재하지 않는 문의")
    void 문의_첨부파일_목록_조회_실패_문의없음() {
        // given
        when(inquiryRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> attachmentService.getByInquiryId(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INQUIRY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 첨부파일 목록 조회 성공")
    void 클레임_첨부파일_목록_조회_성공() {
        // given
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.CLAIM);

        when(claimRepository.existsById(1L)).thenReturn(true);
        when(attachmentRepository.findByClaimIdAndDeletedAtIsNull(1L)).thenReturn(List.of(mockAttachment));

        // when
        List<AttachmentResponseDto> result = attachmentService.getByClaimId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetType()).isEqualTo(Attachment.TargetType.CLAIM);
    }

    @Test
    @DisplayName("클레임 첨부파일 목록 조회 실패 - 존재하지 않는 클레임")
    void 클레임_첨부파일_목록_조회_실패_클레임없음() {
        // given
        when(claimRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> attachmentService.getByClaimId(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    // ── 첨부파일 삭제 ──────────────────────────────────────

    @Test
    @DisplayName("문의 첨부파일 삭제 성공")
    void 문의_첨부파일_삭제_성공() {
        // given
        Inquiry mockInquiry = buildMockInquiry();
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.INQUIRY);

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(mockInquiry));
        doNothing().when(s3Uploader).delete(any());

        // when
        attachmentService.deleteAttachment(1L, 1L);

        // then
        verify(s3Uploader).delete(mockAttachment.getImageUrl());
    }

    @Test
    @DisplayName("첨부파일 삭제 실패 - 존재하지 않는 첨부파일")
    void 첨부파일_삭제_실패_첨부파일없음() {
        // given
        when(attachmentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> attachmentService.deleteAttachment(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ATTACHMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("첨부파일 삭제 실패 - 이미 삭제된 첨부파일")
    void 첨부파일_삭제_실패_이미삭제됨() {
        // given
        Attachment deletedAttachment = buildMockAttachment(Attachment.TargetType.INQUIRY);
        deletedAttachment.delete();

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(deletedAttachment));

        // when & then
        assertThatThrownBy(() -> attachmentService.deleteAttachment(1L, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ATTACHMENT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("첨부파일 삭제 실패 - 권한 없음 (타인 문의)")
    void 첨부파일_삭제_실패_권한없음_문의() {
        // given
        Inquiry mockInquiry = buildMockInquiry(); // memberId = 1L
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.INQUIRY);

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        when(inquiryRepository.findById(1L)).thenReturn(Optional.of(mockInquiry));

        // when & then
        assertThatThrownBy(() -> attachmentService.deleteAttachment(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ATTACHMENT_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("첨부파일 삭제 실패 - 권한 없음 (타인 클레임)")
    void 첨부파일_삭제_실패_권한없음_클레임() {
        // given
        Claim mockClaim = buildMockClaim(); // memberId = 1L
        Attachment mockAttachment = buildMockAttachment(Attachment.TargetType.CLAIM);

        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(mockAttachment));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> attachmentService.deleteAttachment(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ATTACHMENT_UNAUTHORIZED.getMessage());
    }
}
