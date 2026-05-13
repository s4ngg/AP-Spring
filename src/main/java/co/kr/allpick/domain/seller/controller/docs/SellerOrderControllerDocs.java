package co.kr.allpick.domain.seller.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.dto.SellerOrderStatusUpdateRequestDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Seller Order", description = "판매자 주문 현황 API")
public interface SellerOrderControllerDocs {

    @Operation(summary = "판매자 주문 목록 조회", description = "현재 로그인한 판매자의 상품이 포함된 주문 상품 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "판매자 주문 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "판매자 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인되지 않은 판매자")
    })
    ResponseEntity<ApiResponse<List<SellerOrderResponseDto>>> getSellerOrders(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "판매자 주문 상태 변경",
            description = "판매자가 자신의 상품이 포함된 주문의 상태를 변경합니다. PAID → SHIPPING, SHIPPING → DELIVERED 전이만 허용됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "허용되지 않는 상태 전이 또는 미승인 판매자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "판매자 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid SellerOrderStatusUpdateRequestDto request);
}
