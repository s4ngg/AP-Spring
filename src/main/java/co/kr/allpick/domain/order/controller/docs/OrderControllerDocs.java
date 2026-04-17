package co.kr.allpick.domain.order.controller.docs;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Order", description = "주문 API")
public interface OrderControllerDocs {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 생성 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "주문이 생성되었습니다.",
                        "data": {
                            "orderId": 1,
                            "orderNumber": "ORD-20260416-000001",
                            "totalAmount": 20000,
                            "shippingFee": 3000,
                            "status": "PENDING",
                            "orderedAt": "2026-04-16T00:00:00",
                            "orderItems": []
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없습니다.",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "배송지를 찾을 수 없습니다.",
                        "data": null
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @PathVariable Long memberId,
            @RequestBody @Valid OrderCreateRequestDto request);

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "주문 조회 성공.",
                        "data": {
                            "orderId": 1,
                            "orderNumber": "ORD-20260416-000001",
                            "totalAmount": 20000,
                            "shippingFee": 3000,
                            "status": "PENDING",
                            "orderedAt": "2026-04-16T00:00:00",
                            "orderItems": []
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문을 찾을 수 없습니다.",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "주문을 찾을 수 없습니다.",
                        "data": null
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @PathVariable Long orderId);

    @Operation(summary = "결제 정보 조회", description = "주문 ID로 결제 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "결제 정보 조회 성공.",
                        "data": {
                            "paymentId": 1,
                            "paymentKey": "toss_key_123",
                            "method": "CARD",
                            "amount": 23000,
                            "status": "DONE",
                            "paidAt": "2026-04-16T00:00:00"
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 정보를 찾을 수 없습니다.",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "결제 정보를 찾을 수 없습니다.",
                        "data": null
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(
            @PathVariable Long orderId);

    @Operation(summary = "배송지 추가", description = "새로운 배송지를 추가합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 추가 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "배송지가 추가되었습니다.",
                        "data": {
                            "addressId": 1,
                            "recipientName": "홍길동",
                            "phone": "010-1234-5678",
                            "zipCode": "12345",
                            "address": "서울시 강남구",
                            "addressDetail": "101호",
                            "isDefault": false
                        }
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<DeliveryAddressResponseDto>> addDeliveryAddress(
            @PathVariable Long memberId,
            @RequestBody @Valid DeliveryAddressRequestDto request);

    @Operation(summary = "배송지 목록 조회", description = "회원의 배송지 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "배송지 목록 조회 성공.",
                        "data": [
                            {
                                "addressId": 1,
                                "recipientName": "홍길동",
                                "phone": "010-1234-5678",
                                "zipCode": "12345",
                                "address": "서울시 강남구",
                                "addressDetail": "101호",
                                "isDefault": true
                            }
                        ]
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<List<DeliveryAddressResponseDto>>> getDeliveryAddresses(
            @PathVariable Long memberId);
}