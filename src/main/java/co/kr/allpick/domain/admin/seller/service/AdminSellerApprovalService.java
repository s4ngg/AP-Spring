package co.kr.allpick.domain.admin.seller.service;

import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerListResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminSellerApprovalService {

    SellerApprovalResponseDto approveSeller(AdminJwtUserInfoDto adminInfo, Long sellerId);

    SellerApprovalResponseDto rejectSeller(AdminJwtUserInfoDto adminInfo, Long sellerId, SellerRejectRequestDto request);

    // 판매자 목록 (APPROVED + SUSPENDED)
    List<SellerListResponseDto> getSellers(AdminJwtUserInfoDto adminInfo);

    // 승인 대기 목록 (PENDING)
    List<SellerListResponseDto> getPendingSellers(AdminJwtUserInfoDto adminInfo);

    // 상태 토글 (APPROVED ↔ SUSPENDED)
    void toggleSellerStatus(AdminJwtUserInfoDto adminInfo, Long sellerId);
}
