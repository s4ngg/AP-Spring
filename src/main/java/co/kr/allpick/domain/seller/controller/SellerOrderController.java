package co.kr.allpick.domain.seller.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.seller.controller.docs.SellerOrderControllerDocs;
import co.kr.allpick.domain.seller.dto.SellerOrderResponseDto;
import co.kr.allpick.domain.seller.service.SellerOrderService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
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
}
