package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

public interface AdminProductApprovalService {

    ProductApprovalResponseDto approveProduct(AdminJwtUserInfoDto adminInfo, Long productId);

    ProductApprovalResponseDto rejectProduct(AdminJwtUserInfoDto adminInfo, Long productId, ProductRejectRequestDto request);
}
