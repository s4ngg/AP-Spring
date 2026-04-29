package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {

    List<AttachmentResponseDto> uploadInquiryAttachments(Long inquiryId, Long memberId, List<MultipartFile> files);

    List<AttachmentResponseDto> uploadClaimAttachments(Long claimId, Long memberId, List<MultipartFile> files);
}
