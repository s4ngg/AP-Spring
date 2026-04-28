package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    AttachmentResponseDto uploadInquiryAttachment(Long inquiryId, MultipartFile file, int sortOrder);

    AttachmentResponseDto uploadClaimAttachment(Long claimId, MultipartFile file, int sortOrder);

    List<AttachmentResponseDto> getByInquiryId(Long inquiryId);

    List<AttachmentResponseDto> getByClaimId(Long claimId);

    void deleteAttachment(Long attachmentId, Long memberId);
}
