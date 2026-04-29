package co.kr.allpick.domain.admin.product.controller;

import co.kr.allpick.domain.admin.product.controller.docs.AttachmentControllerDocs;
import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.service.AttachmentService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/inquiry/{inquiryId}")
    public ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> uploadInquiryAttachments(
            @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestPart("files") List<MultipartFile> files) {
        return ApiResponse.success("문의 첨부파일이 업로드되었습니다.",
                attachmentService.uploadInquiryAttachments(inquiryId, userInfo.getMemberId(), files));
    }

    @Override
    @PostMapping("/claim/{claimId}")
    public ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> uploadClaimAttachments(
            @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestPart("files") List<MultipartFile> files) {
        return ApiResponse.success("클레임 첨부파일이 업로드되었습니다.",
                attachmentService.uploadClaimAttachments(claimId, userInfo.getMemberId(), files));
    }
}
