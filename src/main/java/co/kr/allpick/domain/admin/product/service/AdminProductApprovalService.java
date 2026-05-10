package co.kr.allpick.domain.admin.product.service;

import co.kr.allpick.domain.admin.product.dto.AdminProductListResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminProductApprovalService {

    List<AdminProductListResponseDto> getProducts(AdminJwtUserInfoDto adminInfo);

    ProductApprovalResponseDto approveProduct(AdminJwtUserInfoDto adminInfo, Long productId);

    ProductApprovalResponseDto rejectProduct(AdminJwtUserInfoDto adminInfo, Long productId, ProductRejectRequestDto request);
}
