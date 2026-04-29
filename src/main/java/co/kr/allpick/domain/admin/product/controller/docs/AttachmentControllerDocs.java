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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 API")
public interface AttachmentControllerDocs {

    @Operation(summary = "문의 첨부파일 업로드", description = "문의에 이미지를 첨부합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="400", description = "파일 없음 또는 이미지 파일 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="404", description = "존재하지 않는 문의")
    })
    ResponseEntity<ApiResponse<AttachmentResponseDto>> uploadInquiryAttachment(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sortOrder") int sortOrder,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "클레임 첨부파일 업로드", description = "클레임에 이미지를 첨부합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="400", description = "파일 없음 또는 이미지 파일 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<AttachmentResponseDto>> uploadClaimAttachment(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("sortOrder") int sortOrder,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "문의 첨부파일 목록 조회", description = "문의 ID로 첨부파일 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="404", description = "존재하지 않는 문의")
    })
    ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> getByInquiryId(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId);

    @Operation(summary = "클레임 첨부파일 목록 조회", description = "클레임 ID로 첨부파일 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<List<AttachmentResponseDto>>> getByClaimId(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId);

    @Operation(summary = "첨부파일 삭제", description = "본인의 첨부파일을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="403", description = "삭제 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode ="404", description = "존재하지 않는 첨부파일")
    })
    ResponseEntity<ApiResponse<Void>> deleteAttachment(
            @Parameter(description = "첨부파일 ID") @PathVariable("attachmentId") Long attachmentId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}
