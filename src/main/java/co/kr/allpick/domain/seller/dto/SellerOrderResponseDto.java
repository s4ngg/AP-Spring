package co.kr.allpick.domain.seller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.kr.allpick.domain.order.entity.DeliveryAddress;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "판매자 주문 현황 응답 DTO")
public class SellerOrderResponseDto {

    @Schema(description = "주문 상품 ID", example = "1")
    private Long orderItemId;

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "주문 번호", example = "ORD-20260503-000001")
    private String orderNumber;

    @Schema(description = "구매자 이름", example = "홍길동")
    private String memberName;

    @Schema(description = "구매자 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "구매자 연락처", example = "010-1234-5678")
    private String memberPhone;

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "올픽 립밤")
    private String productName;

    @Schema(description = "수량", example = "2")
    private int quantity;

    @Schema(description = "주문 상품 결제 금액", example = "20000")
    private BigDecimal totalPrice;

    @Schema(description = "상품 썸네일", example = "https://example.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "주문 상태", example = "PAID")
    private Order.OrderStatus status;

    @Schema(description = "주문 상태 표시명", example = "결제완료")
    private String statusLabel;

    @Schema(description = "주문 일시")
    private LocalDateTime orderedAt;

    @Schema(description = "배송지 정보")
    private DeliveryInfo delivery;

    public static SellerOrderResponseDto from(OrderItem orderItem) {
        Order order = orderItem.getOrder();

        return SellerOrderResponseDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .memberName(order.getMember().getName())
                .memberEmail(order.getMember().getEmail())
                .memberPhone(order.getMember().getPhone())
                .productId(orderItem.getProduct().getProductId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .totalPrice(orderItem.getTotalPrice())
                .thumbnailUrl(orderItem.getProduct().getThumbnailUrl())
                .status(order.getStatus())
                .statusLabel(toStatusLabel(order.getStatus()))
                .orderedAt(order.getOrderedAt())
                .delivery(DeliveryInfo.from(order.getDeliveryAddress()))
                .build();
    }

    private static String toStatusLabel(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> "결제대기";
            case PAID -> "결제완료";
            case SHIPPING -> "배송중";
            case DELIVERED -> "배송완료";
            case CANCELLED -> "취소";
        };
    }

    @Getter
    @Builder
    @Schema(description = "판매자 주문 배송지 응답 DTO")
    public static class DeliveryInfo {

        @Schema(description = "수령인", example = "홍길동")
        private String recipientName;

        @Schema(description = "수령인 연락처", example = "010-1234-5678")
        private String phone;

        @Schema(description = "우편번호", example = "12345")
        private String zipCode;

        @Schema(description = "주소", example = "서울시 강남구")
        private String address;

        @Schema(description = "상세주소", example = "101호")
        private String addressDetail;

        public static DeliveryInfo from(DeliveryAddress deliveryAddress) {
            return DeliveryInfo.builder()
                    .recipientName(deliveryAddress.getRecipientName())
                    .phone(deliveryAddress.getPhone())
                    .zipCode(deliveryAddress.getZipCode())
                    .address(deliveryAddress.getAddress())
                    .addressDetail(deliveryAddress.getAddressDetail())
                    .build();
        }
    }
}
