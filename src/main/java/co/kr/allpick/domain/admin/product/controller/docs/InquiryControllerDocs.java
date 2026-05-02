package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@Tag(name = "Inquiry", description = "문의 API")
public interface InquiryControllerDocs {

    @Operation(summary = "문의 등록", description = "회원이 문의를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 주문 상품")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<InquiryResponseDto>> createInquiry(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid InquiryCreateRequestDto request);

    @Operation(summary = "문의 상세 조회", description = "문의 ID로 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 문의")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<InquiryResponseDto>> getInquiryById(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId);

    @Operation(summary = "내 문의 목록 조회", description = "JWT 토큰으로 인증된 회원의 문의 목록을 조회합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<List<InquiryResponseDto>>> getMyInquiries(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "전체 문의 목록 조회", description = "관리자가 전체 문의 목록을 조회합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<List<InquiryResponseDto>>> getAllInquiries();

    @Operation(summary = "관리자 답변 등록", description = "관리자가 JWT 토큰 기반으로 답변을 등록합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<InquiryAnswerResponseDto>> addAdminAnswer(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDto request,
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo);

    @Operation(summary = "판매자 답변 등록", description = "판매자가 JWT 토큰 기반으로 답변을 등록합니다. (판매자 JWT 구현 후 연결 예정)")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<InquiryAnswerResponseDto>> addSellerAnswer(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @RequestBody @Valid InquiryAnswerRequestDto request,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "문의 취소", description = "접수 대기 상태인 문의를 취소합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<Void>> cancelInquiry(
            @Parameter(description = "문의 ID") @PathVariable("inquiryId") Long inquiryId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}
