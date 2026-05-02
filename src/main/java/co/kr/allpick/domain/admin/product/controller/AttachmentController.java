package co.kr.allpick.domain.admin.product.controller;

import co.kr.allpick.domain.admin.product.controller.docs.AttachmentControllerDocs;
import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.service.AttachmentService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttachmentController implements AttachmentControllerDocs {

    private final AttachmentService attachmentService;

    @Override
    @PostMapping(value = "/inquiry/{inquiryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AttachmentResponseDto>> uploadInquiryAttachment(
            @PathVariable("inquiryId") Long inquiryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sortOrder") int sortOrder,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("첨부파일이 등록되었습니다.",
                attachmentService.uploadInquiryAttachment(inquiryId, file, sortOrder));
    }

    @Override
    @PostMapping(value = "/claim/{claimId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AttachmentResponseDto>> uploadClaimAttachment(
            @PathVariable("claimId") Long claimId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sortOrder") int sortOrder,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("첨부파일이 등록되었습니다.",
                attachmentService.uploadClaimAttachment(claimId, file, sortOrder));
    }

    @Override
    @GetMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> getByInquiryId(
            @PathVariable("inquiryId") Long inquiryId) {
        return ApiResponse.success("문의 첨부파일 목록 조회 성공.", attachmentService.getByInquiryId(inquiryId));
    }

    @Override
    @GetMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> getByClaimId(
            @PathVariable("claimId") Long claimId) {
        return ApiResponse.success("클레임 첨부파일 목록 조회 성공.", attachmentService.getByClaimId(claimId));
    }

    @Override
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @PathVariable("attachmentId") Long attachmentId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        attachmentService.deleteAttachment(attachmentId, userInfo.getMemberId());
        return ApiResponse.success("첨부파일이 삭제되었습니다.", null);
    }
}
