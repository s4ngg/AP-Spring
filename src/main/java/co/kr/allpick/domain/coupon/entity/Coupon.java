package co.kr.allpick.domain.coupon.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_order_amount", nullable = false)
    private BigDecimal minOrderAmount;

    @Column(name = "max_discount")
    private BigDecimal maxDiscount;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public Coupon(String couponCode, DiscountType discountType, BigDecimal discountValue,
                  BigDecimal minOrderAmount, BigDecimal maxDiscount, LocalDateTime expiredAt) {
        this.couponCode = couponCode;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxDiscount = maxDiscount;
        this.expiredAt = expiredAt;
    }

    public enum DiscountType {
        PERCENT, AMOUNT
    }
}