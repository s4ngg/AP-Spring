package co.kr.allpick.domain.admin.product.controller.docs;

import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Admin Product Approval", description = "관리자 상품 승인 API")
public interface AdminProductApprovalControllerDocs {

    @Operation(summary = "상품 승인", description = "SUPER_ADMIN이 PENDING 상태의 상품을 승인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인 가능한 상태가 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음")
    })
    ResponseEntity<ApiResponse<ProductApprovalResponseDto>> approveProduct(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @Parameter(description = "상품 ID") @PathVariable("productId") Long productId);

    @Operation(summary = "상품 승인 거절", description = "SUPER_ADMIN이 PENDING 상태의 상품을 거절합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 거절 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "거절 가능한 상태가 아니거나 거절 사유 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음")
    })
    ResponseEntity<ApiResponse<ProductApprovalResponseDto>> rejectProduct(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @Parameter(description = "상품 ID") @PathVariable("productId") Long productId,
            @RequestBody @Valid ProductRejectRequestDto request);
}
