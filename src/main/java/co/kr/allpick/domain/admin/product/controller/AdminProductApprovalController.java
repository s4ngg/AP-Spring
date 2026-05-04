package co.kr.allpick.domain.admin.product.controller;

import co.kr.allpick.domain.admin.product.controller.docs.AdminProductApprovalControllerDocs;
import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.domain.admin.product.service.AdminProductApprovalService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductApprovalController implements AdminProductApprovalControllerDocs {

    private final AdminProductApprovalService adminProductApprovalService;

    @Override
    @PatchMapping("/{productId}/approve")
    public ResponseEntity<ApiResponse<ProductApprovalResponseDto>> approveProduct(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("productId") Long productId) {
        return ApiResponse.success("상품 승인 성공", adminProductApprovalService.approveProduct(adminInfo, productId));
    }

    @Override
    @PatchMapping("/{productId}/reject")
    public ResponseEntity<ApiResponse<ProductApprovalResponseDto>> rejectProduct(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("productId") Long productId,
            @RequestBody @Valid ProductRejectRequestDto request) {
        return ApiResponse.success("상품 승인 거절 성공", adminProductApprovalService.rejectProduct(adminInfo, productId, request));
    }
}
