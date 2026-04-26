package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
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

@Tag(name = "Claim", description = "클레임(교환/반품) API")
public interface ClaimControllerDocs {

    @Operation(summary = "클레임 등록", description = "회원이 교환 또는 반품 클레임을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클레임 등록 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 이미 진행 중인 클레임 존재"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 주문 상품")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<ClaimResponseDto>> createClaim(
            @RequestBody @Valid ClaimCreateRequestDto request);

    @Operation(summary = "클레임 상세 조회", description = "클레임 ID로 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클레임 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<ClaimResponseDto>> getClaimById(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId);

    @Operation(summary = "내 클레임 목록 조회", description = "JWT 토큰으로 인증된 회원의 클레임 목록을 조회합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<List<ClaimResponseDto>>> getMyClaims(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "전체 클레임 목록 조회", description = "관리자가 전체 클레임 목록을 조회합니다.")
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<List<ClaimResponseDto>>> getAllClaims();

    @Operation(summary = "클레임 상태 변경", description = "관리자 또는 판매자가 클레임 상태를 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클레임 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "이미 완료되거나 거부된 클레임"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<ClaimResponseDto>> updateStatus(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimStatusUpdateRequestDto request);

    @Operation(summary = "클레임 거부", description = "관리자 또는 판매자가 클레임을 거부합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클레임 거부 성공"),
            @ApiResponse(responseCode = "400", description = "이미 완료되거나 거부된 클레임"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<ClaimResponseDto>> rejectClaim(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimRejectRequestDto request);

    @Operation(summary = "클레임 취소", description = "접수(SUBMITTED) 상태인 클레임을 취소합니다. 접수 중(IN_PROGRESS) 이상은 취소 불가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "클레임 취소 성공"),
            @ApiResponse(responseCode = "400", description = "접수 중 이상의 클레임은 취소 불가"),
            @ApiResponse(responseCode = "403", description = "해당 클레임에 대한 권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 클레임")
    })
    ResponseEntity<co.kr.allpick.global.response.ApiResponse<Void>> cancelClaim(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @AuthenticationPrincipal JwtUserInfoDto userInfo);
}
