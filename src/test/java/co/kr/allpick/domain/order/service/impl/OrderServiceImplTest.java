package co.kr.allpick.domain.order.controller.docs;

import co.kr.allpick.domain.order.dto.*;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "주문 및 배송지 관련 API")
public interface OrderControllerDocs {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "주문이 생성되었습니다.",
                    "data": {
                        "orderId": 1,
                        "orderNumber": "ORD-20260503-000001",
                        "totalAmount": 50000,
                        "shippingFee": 3000,
                        "status": "PENDING",
                        "orderedAt": "2026-05-03T22:00:00"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "재고 부족 또는 잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 또는 옵션을 찾을 수 없음")
    })
    @PostMapping
    ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid OrderCreateRequestDto request);

    @Operation(summary = "주문 목록 조회", description = "결제 완료된 주문 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회 성공")
    })
    @GetMapping
    ResponseEntity<ApiResponse<List<OrderResponseDto>>> getOrders(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 상세 주문 내역을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @GetMapping("/{orderId}")
    ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(@PathVariable("orderId") Long orderId);

    @Operation(summary = "주문 취소", description = "현재 로그인한 회원의 주문을 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소할 수 없는 주문 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 주문이 아님"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음")
    })
    @PatchMapping("/{orderId}/cancel")
    ResponseEntity<ApiResponse<OrderResponseDto>> cancelOrder(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable("orderId") Long orderId);

    @Operation(summary = "결제 정보 조회", description = "주문 ID에 연결된 결제 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 정보 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "결제 정보 조회 성공.",
                    "data": {
                        "paymentId": 1,
                        "paymentKey": "toss_key_123",
                        "method": "CARD",
                        "amount": 53000,
                        "status": "DONE",
                        "paidAt": "2026-05-03T22:05:00"
                    }
                }
            """)))
    })
    @GetMapping("/{orderId}/payment")
    ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(@PathVariable("orderId") Long orderId);

    @Operation(summary = "결제 확인", description = "토스 결제 완료 후 주문 상태를 PAID로 변경하고 결제 정보를 저장합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 확인 성공")
    })
    @PostMapping("/confirm")
    ResponseEntity<ApiResponse<OrderResponseDto>> confirmPayment(
            @RequestParam String orderNumber,
            @RequestParam String paymentKey,
            @RequestParam int amount);

    @Operation(summary = "배송지 추가", description = "회원의 새로운 배송지를 추가합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 추가 성공")
    })
    @PostMapping("/addresses")
    ResponseEntity<ApiResponse<DeliveryAddressResponseDto>> addDeliveryAddress(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid DeliveryAddressRequestDto request);

    @Operation(summary = "배송지 목록 조회", description = "현재 로그인한 사용자의 모든 배송지 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "배송지 목록 조회 성공.",
                    "data": [
                        {
                            "addressId": 1,
                            "recipientName": "홍길동",
                            "phone": "01012345678",
                            "zipCode": "12345",
                            "address": "서울시 강남구",
                            "addressDetail": "101호",
                            "isDefault": true
                        }
                    ]
                }
            """)))
    })
    @GetMapping("/addresses")
    ResponseEntity<ApiResponse<List<DeliveryAddressResponseDto>>> getDeliveryAddresses(
            @AuthenticationPrincipal JwtUserInfoDto userInfo);

    @Operation(summary = "배송지 수정", description = "기존 배송지 정보를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "배송지 존재하지 않음")
    })
    @PatchMapping("/addresses/{addressId}")
    ResponseEntity<ApiResponse<DeliveryAddressResponseDto>> updateDeliveryAddress(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable("addressId") Long addressId,
            @RequestBody @Valid DeliveryAddressRequestDto request);

    @Operation(summary = "배송지 삭제", description = "배송지를 삭제 처리합니다. (소프트 딜리트)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 삭제 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "배송지가 삭제되었습니다.",
                    "data": null
                }
            """)))
    })
    @DeleteMapping("/addresses/{addressId}")
    ResponseEntity<ApiResponse<Void>> deleteDeliveryAddress(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @PathVariable("addressId") Long addressId);
}