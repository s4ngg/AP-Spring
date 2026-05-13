package co.kr.allpick.domain.seller.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.seller.controller.docs.SellerOrderControllerDocs;
import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.dto.SellerOrderStatusUpdateRequestDto;
import co.kr.allpick.domain.seller.service.SellerOrderService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
public class SellerOrderController implements SellerOrderControllerDocs {

    private final SellerOrderService sellerOrderService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<SellerOrderResponseDto>>> getSellerOrders(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("판매자 주문 목록 조회 성공", sellerOrderService.getSellerOrders(userInfo.getMemberId()));
    }

    @Override
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable("orderId") Long orderId,
            @RequestBody @Valid SellerOrderStatusUpdateRequestDto request) {
        sellerOrderService.updateOrderStatus(userInfo.getMemberId(), orderId, request.getStatus());
        return ApiResponse.success("주문 상태가 변경되었습니다.");
    }
}
