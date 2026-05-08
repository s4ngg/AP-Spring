package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SellerOrderResponseDto {

    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private int shippingFee;
    private Order.OrderStatus status;
    private LocalDateTime orderedAt;

    private String memberName;
    private String memberEmail;
    private String memberPhone;

    private String recipientName;
    private String recipientPhone;
    private String zipCode;
    private String address;
    private String addressDetail;

    private List<OrderItemResponseDto> orderItems;

    public static SellerOrderResponseDto from(Order order, List<OrderItem> items) {
        return SellerOrderResponseDto.builder()
                .orderId(order.getOrderId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .memberName(order.getMember().getName())
                .memberEmail(order.getMember().getEmail())
                .memberPhone(order.getMember().getPhone())
                .recipientName(order.getDeliveryAddress().getRecipientName())
                .recipientPhone(order.getDeliveryAddress().getPhone())
                .zipCode(order.getDeliveryAddress().getZipCode())
                .address(order.getDeliveryAddress().getAddress())
                .addressDetail(order.getDeliveryAddress().getAddressDetail())
                .orderItems(items.stream()
                        .map(OrderItemResponseDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
