package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface ClaimService {

    // 클레임 등록
    ClaimResponseDto createClaim(Long memberId, ClaimCreateRequestDto request);

    // 클레임 상세 조회
    ClaimResponseDto getClaimById(Long claimId);

    // 내 클레임 목록 조회
    List<ClaimResponseDto> getMyClaims(Long memberId);

    // 전체 클레임 목록 조회 (관리자)
    List<ClaimResponseDto> getAllClaims(AdminJwtUserInfoDto adminInfo);

    // 클레임 상태 변경 (관리자)
    ClaimResponseDto updateStatus(AdminJwtUserInfoDto adminInfo, Long claimId, ClaimStatusUpdateRequestDto request);

    // 클레임 거부 (관리자)
    ClaimResponseDto rejectClaim(AdminJwtUserInfoDto adminInfo, Long claimId, ClaimRejectRequestDto request);

    // 클레임 취소 (회원)
    void cancelClaim(Long claimId, Long memberId);
    
    // 클레임 승인 (판매자)
    ClaimResponseDto approveClaim(Long claimId, Long memberId);
    
    // 클레임 거부 (판매자, 현재 지원하지 않음)
    ClaimResponseDto rejectClaimBySeller(Long claimId, ClaimRejectRequestDto request, Long memberId);
    
    // 판매자 클레임 목록 조회
    List<ClaimResponseDto> getSellerClaims(Long memberId);
    
    
}
