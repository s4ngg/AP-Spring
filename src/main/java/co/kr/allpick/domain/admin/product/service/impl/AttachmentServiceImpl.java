package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.entity.Attachment;
import co.kr.allpick.domain.admin.product.repository.AttachmentRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryRepository;
import co.kr.allpick.domain.admin.product.service.AttachmentService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import co.kr.allpick.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger logger = LogManager.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;
    private final InquiryRepository inquiryRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public List<AttachmentResponseDto> uploadInquiryAttachments(Long inquiryId, Long memberId, List<MultipartFile> files) {
        logger.info("[AttachmentService] 문의 첨부파일 업로드 - inquiryId: {}, memberId: {}", inquiryId, memberId);

        var inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        if (!inquiry.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.INQUIRY_UNAUTHORIZED);
        }

        List<Attachment> attachments = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                String imageUrl = s3Service.upload(files.get(i), "inquiry");
                attachments.add(Attachment.builder()
                        .inquiryId(inquiryId)
                        .targetType(Attachment.TargetType.INQUIRY)
                        .imageUrl(imageUrl)
                        .sortOrder(i)
                        .build());
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return attachmentRepository.saveAll(attachments)
                .stream()
                .map(AttachmentResponseDto::from)
                .toList();
    }

    // TODO: cs-claim 브랜치 머지 후 ClaimRepository 주입 및 소유권 검증 추가
    @Override
    @Transactional
    public List<AttachmentResponseDto> uploadClaimAttachments(Long claimId, Long memberId, List<MultipartFile> files) {
        logger.info("[AttachmentService] 클레임 첨부파일 업로드 - claimId: {}, memberId: {}", claimId, memberId);
        throw new BusinessException(ErrorCode.NOT_IMPLEMENTED);
    }
}
