package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.entity.Attachment;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import co.kr.allpick.domain.admin.product.repository.AttachmentRepository;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryRepository;
import co.kr.allpick.domain.admin.product.service.AttachmentService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger logger = LogManager.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;
    private final InquiryRepository inquiryRepository;
    private final ClaimRepository claimRepository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public AttachmentResponseDto uploadInquiryAttachment(Long inquiryId, MultipartFile file, int sortOrder) {
        logger.info("[AttachmentService] 문의 첨부파일 업로드 - inquiryId: {}", inquiryId);

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        if (inquiry.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.INQUIRY_ALREADY_DELETED);
        }

        String imageUrl = s3Uploader.upload(file, "inquiries");

        Attachment attachment = attachmentRepository.save(Attachment.builder()
                .inquiryId(inquiryId)
                .claimId(null)
                .targetType(Attachment.TargetType.INQUIRY)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder)
                .build());

        return AttachmentResponseDto.from(attachment);
    }

    @Override
    @Transactional
    public AttachmentResponseDto uploadClaimAttachment(Long claimId, MultipartFile file, int sortOrder) {
        logger.info("[AttachmentService] 클레임 첨부파일 업로드 - claimId: {}", claimId);

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        if (claim.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_DELETED);
        }

        String imageUrl = s3Uploader.upload(file, "claims");

        Attachment attachment = attachmentRepository.save(Attachment.builder()
                .inquiryId(null)
                .claimId(claimId)
                .targetType(Attachment.TargetType.CLAIM)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder)
                .build());

        return AttachmentResponseDto.from(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> getByInquiryId(Long inquiryId) {
        if (!inquiryRepository.existsById(inquiryId)) {
            throw new BusinessException(ErrorCode.INQUIRY_NOT_FOUND);
        }
        return attachmentRepository.findByInquiryIdAndDeletedAtIsNull(inquiryId)
                .stream()
                .map(AttachmentResponseDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> getByClaimId(Long claimId) {
        if (!claimRepository.existsById(claimId)) {
            throw new BusinessException(ErrorCode.CLAIM_NOT_FOUND);
        }
        return attachmentRepository.findByClaimIdAndDeletedAtIsNull(claimId)
                .stream()
                .map(AttachmentResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId, Long memberId) {
        logger.info("[AttachmentService] 첨부파일 삭제 - attachmentId: {}", attachmentId);

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTACHMENT_NOT_FOUND));

        if (attachment.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ATTACHMENT_ALREADY_DELETED);
        }

        validateOwnership(attachment, memberId);

        s3Uploader.delete(attachment.getImageUrl());
        attachment.delete();
    }

    private void validateOwnership(Attachment attachment, Long memberId) {
        if (attachment.getTargetType() == Attachment.TargetType.INQUIRY) {
            Inquiry inquiry = inquiryRepository.findById(attachment.getInquiryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));
            if (!inquiry.getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ATTACHMENT_UNAUTHORIZED);
            }
        } else {
            Claim claim = claimRepository.findById(attachment.getClaimId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));
            if (!claim.getMemberId().equals(memberId)) {
                throw new BusinessException(ErrorCode.ATTACHMENT_UNAUTHORIZED);
            }
        }
    }
}
