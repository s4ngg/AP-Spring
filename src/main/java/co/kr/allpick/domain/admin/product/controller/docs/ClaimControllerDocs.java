package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Claim", description = "클레임(교환/반품) API")
public interface ClaimControllerDocs {

    @Operation(summary = "클레임 등록", description = "회원이 교환 또는 반품 클레임을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 이미 진행 중인 클레임 존재"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 주문 상품")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> createClaim(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid ClaimCreateRequestDto request);

    @Operation(summary = "클레임 상세 조회", description = "클레임 ID로 상세 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> getClaimById(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId);

    @Operation(summary = "내 클레임 목록 조회", description = "JWT 토큰으로 인증된 회원의 클레임 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getMyClaims(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "전체 클레임 목록 조회", description = "관리자가 전체 클레임 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getAllClaims(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo);

    @Operation(summary = "판매자 클레임 목록 조회", description = "JWT 토큰으로 인증된 판매자의 클레임 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "판매자 클레임 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 판매자")
    })
    ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getSellerClaims(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
    @Operation(summary = "클레임 상태 변경", description = "관리자가 판매자 확인(IN_PROGRESS)이 끝난 클레임을 완료(COMPLETED) 처리합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "판매자 확인 전이거나 이미 완료/거부된 클레임"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> updateStatus(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimStatusUpdateRequestDto request);

    @Operation(summary = "클레임 거부", description = "관리자가 판매자 확인(IN_PROGRESS)이 끝난 클레임을 거부합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 거부 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "판매자 확인 전이거나 이미 완료/거부된 클레임"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> rejectClaim(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimRejectRequestDto request);

    @Operation(summary = "클레임 취소", description = "접수(SUBMITTED) 상태인 클레임을 취소합니다. 접수 중(IN_PROGRESS) 이상은 취소 불가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "접수 중 외의 클레임은 취소 불가"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 클레임에 대한 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<ApiResponse<Void>> cancelClaim(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
    
    @Operation(summary = "클레임 승인 (판매자)", description = "판매자가 자신의 상품 클레임을 승인합니다. SUBMITTED → IN_PROGRESS")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "클레임 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "접수 상태가 아닌 클레임"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 클레임에 대한 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임 또는 판매자")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> approveClaim(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId);

    @Operation(summary = "클레임 거부 (판매자, 지원하지 않음)", description = "판매자는 클레임을 최종 거부하지 않습니다. 판매자 승인 후 관리자가 최종 승인 또는 거부합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "판매자 거부는 지원하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "해당 클레임에 대한 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 클레임 또는 판매자")
    })
    ResponseEntity<ApiResponse<ClaimResponseDto>> rejectClaimBySeller(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimRejectRequestDto request);
}
