package co.kr.allpick.domain.admin.seller.controller;

import co.kr.allpick.domain.admin.seller.controller.docs.AdminSellerApprovalControllerDocs;
import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.domain.admin.seller.service.AdminSellerApprovalService;
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
@RequestMapping("/api/admin/sellers")
public class AdminSellerApprovalController implements AdminSellerApprovalControllerDocs {

    private final AdminSellerApprovalService adminSellerApprovalService;

    @Override
    @PatchMapping("/{sellerId}/approve")
    public ResponseEntity<ApiResponse<SellerApprovalResponseDto>> approveSeller(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("sellerId") Long sellerId) {
        return ApiResponse.success("판매자 승인 성공", adminSellerApprovalService.approveSeller(adminInfo, sellerId));
    }

    @Override
    @PatchMapping("/{sellerId}/reject")
    public ResponseEntity<ApiResponse<SellerApprovalResponseDto>> rejectSeller(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable("sellerId") Long sellerId,
            @RequestBody @Valid SellerRejectRequestDto request) {
        return ApiResponse.success("판매자 승인 거절 성공", adminSellerApprovalService.rejectSeller(adminInfo, sellerId, request));
    }
}
