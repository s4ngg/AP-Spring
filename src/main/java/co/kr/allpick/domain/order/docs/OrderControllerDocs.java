package co.kr.allpick.domain.order.docs;

import co.kr.allpick.domain.order.dto.DeliveryAddressRequestDto;
import co.kr.allpick.domain.order.dto.DeliveryAddressResponseDto;
import co.kr.allpick.domain.order.dto.OrderCreateRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.dto.PaymentResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Order", description = "주문 API")
public interface OrderControllerDocs {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(
            @PathVariable Long memberId,
            @RequestBody OrderCreateRequestDto request);

    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문을 조회합니다.")
    ResponseEntity<ApiResponse<OrderResponseDto>> getOrder(
            @PathVariable Long orderId);

    @Operation(summary = "결제 정보 조회", description = "주문 ID로 결제 정보를 조회합니다.")
    ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(
            @PathVariable Long orderId);

    @Operation(summary = "배송지 추가", description = "새로운 배송지를 추가합니다.")
    ResponseEntity<ApiResponse<DeliveryAddressResponseDto>> addDeliveryAddress(
            @PathVariable Long memberId,
            @RequestBody DeliveryAddressRequestDto request);

    @Operation(summary = "배송지 목록 조회", description = "회원의 배송지 목록을 조회합니다.")
    ResponseEntity<ApiResponse<List<DeliveryAddressResponseDto>>> getDeliveryAddresses(
            @PathVariable Long memberId);
}