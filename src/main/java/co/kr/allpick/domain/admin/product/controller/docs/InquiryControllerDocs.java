package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import co.kr.allpick.global.config.JwtUserInfoDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@Tag(name = "Inquiry", description = "문의 API")
public interface InquiryControllerDocs {

    @Operation(summary = "문의 등록", description = "회원이 문의를 등록합니다.")
    ResponseEntity<ApiResponse<InquiryResponseDto>> createInquiry(
            @RequestBody @Valid InquiryCreateRequestDto request);

    @Operation(summary = "문의 상세 조회", description = "문의 ID로 상세 조회합니다.")
    ResponseEntity<ApiResponse<InquiryResponseDto>> getInquiryById(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId);

    @Operation(summary = "내 문의 목록 조회", description = "JWT 토큰으로 인증된 회원의 문의 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<InquiryResponseDto>>> getMyInquiries(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "전체 문의 목록 조회", description = "관리자가 전체 문의 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<InquiryResponseDto>>> getAllInquiries();

    @Operation(summary = "답변 등록", description = "관리자 또는 판매자가 JWT 토큰 기반으로 답변을 등록합니다.")
    ResponseEntity<ApiResponse<InquiryAnswerResponseDto>> addAnswer(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDto request,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "문의 취소", description = "접수 대기 상태인 문의를 취소합니다.")
    ResponseEntity<ApiResponse<Void>> cancelInquiry(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}