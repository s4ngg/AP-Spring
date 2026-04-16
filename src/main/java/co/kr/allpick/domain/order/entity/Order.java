package co.kr.allpick.domain.order.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "shipping_fee", nullable = false)
    private int shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(Long memberId, Long addressId, String orderNumber,
                 BigDecimal totalAmount, int shippingFee,
                 OrderStatus status, LocalDateTime orderedAt) {
        this.memberId = memberId;
        this.addressId = addressId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.shippingFee = shippingFee;
        this.status = status;
        this.orderedAt = orderedAt;
    }

    public void updateTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public enum OrderStatus {
        PENDING, PAID, SHIPPING, DELIVERED, CANCELLED
    }
}