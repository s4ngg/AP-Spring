package co.kr.allpick.domain.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import co.kr.allpick.domain.coupon.entity.MemberCoupon;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_coupon_id", nullable = false)
    private MemberCoupon memberCoupon;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private DeliveryAddress deliveryAddress;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee", nullable = false)
    private int shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // 편의 메서드 : order의 부모지정 메서드를 호출해서 연결.
    public void addOrderItem(OrderItem orderItem) {
    	this.orderItems.add(orderItem);
    	orderItem.assignOrder(this);
    }

    @Builder
    public Order(Member member, MemberCoupon memberCoupon , DeliveryAddress deliveryAddress, 
    			 Long addressId, 
                 String orderNumber, BigDecimal totalAmount, BigDecimal discountAmount,
                 int shippingFee, OrderStatus status, LocalDateTime orderedAt) {
    	
        this.member = member;
        this.memberCoupon =memberCoupon;
        this.deliveryAddress = deliveryAddress;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
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