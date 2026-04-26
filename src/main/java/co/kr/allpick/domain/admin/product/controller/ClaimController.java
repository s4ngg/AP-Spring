package co.kr.allpick.domain.admin.product.controller;

import co.kr.allpick.domain.admin.product.controller.docs.ClaimControllerDocs;
import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.service.ClaimService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/claims")
public class ClaimController implements ClaimControllerDocs {

    private final ClaimService claimService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<ClaimResponseDto>> createClaim(
            @RequestBody @Valid ClaimCreateRequestDto request) {
        return ApiResponse.success("클레임이 등록되었습니다.", claimService.createClaim(request));
    }

    @Override
    @GetMapping("/{claimId}")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> getClaimById(
            @PathVariable("claimId") Long claimId) {
        return ApiResponse.success("클레임 조회 성공.", claimService.getClaimById(claimId));
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getMyClaims(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("내 클레임 목록 조회 성공.", claimService.getMyClaims(userInfo.getMemberId()));
    }

    @Override
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<ClaimResponseDto>>> getAllClaims() {
        return ApiResponse.success("전체 클레임 목록 조회 성공.", claimService.getAllClaims());
    }

    @Override
    @PatchMapping("/{claimId}/status")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> updateStatus(
            @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimStatusUpdateRequestDto request) {
        return ApiResponse.success("클레임 상태가 변경되었습니다.", claimService.updateStatus(claimId, request));
    }

    @Override
    @PatchMapping("/{claimId}/reject")
    public ResponseEntity<ApiResponse<ClaimResponseDto>> rejectClaim(
            @PathVariable("claimId") Long claimId,
            @RequestBody @Valid ClaimRejectRequestDto request) {
        return ApiResponse.success("클레임이 거부되었습니다.", claimService.rejectClaim(claimId, request));
    }
}
