package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 API")
public interface AttachmentControllerDocs {

    @Operation(summary = "문의 첨부파일 업로드", description = "문의에 이미지 파일을 첨부합니다. 본인 문의에만 업로드 가능합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 문의가 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 문의")
    })
    ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> uploadInquiryAttachments(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestPart("files") List<MultipartFile> files);

    @Operation(summary = "클레임 첨부파일 업로드", description = "클레임에 이미지 파일을 첨부합니다. 본인 클레임에만 업로드 가능합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 클레임이 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> uploadClaimAttachments(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestPart("files") List<MultipartFile> files);
}
