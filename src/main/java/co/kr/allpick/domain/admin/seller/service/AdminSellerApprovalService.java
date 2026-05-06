package co.kr.allpick.domain.admin.seller.service;

import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

public interface AdminSellerApprovalService {

    SellerApprovalResponseDto approveSeller(AdminJwtUserInfoDto adminInfo, Long sellerId);

    SellerApprovalResponseDto rejectSeller(AdminJwtUserInfoDto adminInfo, Long sellerId, SellerRejectRequestDto request);
}
