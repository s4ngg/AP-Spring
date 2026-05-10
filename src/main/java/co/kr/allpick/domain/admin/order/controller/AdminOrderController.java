package co.kr.allpick.domain.admin.order.controller;

import co.kr.allpick.domain.admin.order.controller.docs.AdminOrderControllerDocs;
import co.kr.allpick.domain.admin.order.dto.AdminOrderResponseDto;
import co.kr.allpick.domain.admin.order.service.AdminOrderService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController implements AdminOrderControllerDocs {

    private final AdminOrderService adminOrderService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminOrderResponseDto>>> getOrders(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo) {
        return ApiResponse.success("관리자 주문 목록 조회 성공", adminOrderService.getOrders(adminInfo));
    }
}
