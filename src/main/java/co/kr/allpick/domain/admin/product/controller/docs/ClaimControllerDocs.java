package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    ResponseEntity<ApiResponse<ClaimResponseDto>> createClaim(
            @RequestBody @Valid ClaimCreateRequestDto request);

    @Operation(summary = "클레임 상세 조회", description = "클레임 ID로 상세 조회합니다.")
    ResponseEntity<ApiResponse<ClaimResponseDto>> getClaimById(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId);

    @Operation(summary = "내 클레임 목록 조회", description = "JWT 토큰으로 인증된 회원의 클레임 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getMyClaims(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "전체 클레임 목록 조회", description = "관리자가 전체 클레임 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getAllClaims();

    @Operation(summary = "클레임 상태 변경", description = "관리자 또는 판매자가 클레임 상태를 변경합니다.")
    ResponseEntity<ApiResponse<ClaimResponseDto>> updateStatus(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimStatusUpdateRequestDto request);

    @Operation(summary = "클레임 거부", description = "관리자 또는 판매자가 클레임을 거부합니다.")
    ResponseEntity<ApiResponse<ClaimResponseDto>> rejectClaim(
            @Parameter(description = "클레임 ID") @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimRejectRequestDto request);
}
