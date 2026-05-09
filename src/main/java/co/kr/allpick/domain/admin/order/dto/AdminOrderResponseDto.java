package co.kr.allpick.domain.admin.order.dto;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "관리자 주문 목록 응답 DTO")
public class AdminOrderResponseDto {

    @Schema(description = "주문 ID", example = "1")
    private Long orderId;

    @Schema(description = "주문 번호", example = "ORD-20260503-000001")
    private String orderNumber;

    @Schema(description = "주문 회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "주문자 이름", example = "홍길동")
    private String memberName;

    @Schema(description = "주문자 이메일", example = "user@example.com")
    private String memberEmail;

    @Schema(description = "대표 상품명", example = "나이키 운동화 외 1건")
    private String productName;

    @Schema(description = "총 결제 금액", example = "53000")
    private BigDecimal totalAmount;

    @Schema(description = "주문 상태", example = "PAID")
    private Order.OrderStatus status;

    @Schema(description = "주문 일시")
    private LocalDateTime orderedAt;

    public static AdminOrderResponseDto from(Order order) {
        return AdminOrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .memberId(order.getMember().getId())
                .memberName(order.getMember().getName())
                .memberEmail(order.getMember().getEmail())
                .productName(buildProductName(order.getOrderItems()))
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .build();
    }

    private static String buildProductName(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return "-";
        }
        String firstProductName = orderItems.get(0).getProductName();
        if (orderItems.size() == 1) {
            return firstProductName;
        }
        return firstProductName + " 외 " + (orderItems.size() - 1) + "건";
    }
}
