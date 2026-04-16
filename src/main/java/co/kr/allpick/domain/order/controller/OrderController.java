package co.kr.allpick.domain.order.controller;

import co.kr.allpick.domain.order.docs.OrderControllerDocs;
import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.domain.order.service.OrderService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    @Override
    @PostMapping("/{memberId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @PathVariable Long memberId,
            @RequestBody OrderCreateRequestDto request) {
        return ApiResponse.success("주문이 생성되었습니다.", orderService.createOrder(memberId, request));
    }

    @Override
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @PathVariable Long orderId) {
        return ApiResponse.success("주문 조회 성공.", orderService.getOrder(orderId));
    }

    @Override
    @GetMapping("/{orderId}/payment")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(
            @PathVariable Long orderId) {
        return ApiResponse.success("결제 정보 조회 성공.", orderService.getPayment(orderId));
    }

    @Override
    @PostMapping("/{memberId}/addresses")
    public ResponseEntity<ApiResponse<DeliveryAddressResponseDto>> addDeliveryAddress(
            @PathVariable Long memberId,
            @RequestBody DeliveryAddressRequestDto request) {
        return ApiResponse.success("배송지가 추가되었습니다.", orderService.addDeliveryAddress(memberId, request));
    }

    @Override
    @GetMapping("/{memberId}/addresses")
    public ResponseEntity<ApiResponse<List<DeliveryAddressResponseDto>>> getDeliveryAddresses(
            @PathVariable Long memberId) {
        return ApiResponse.success("배송지 목록 조회 성공.", orderService.getDeliveryAddresses(memberId));
    }
}