package co.kr.allpick.domain.admin.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Getter
@NoArgsConstructor
public class Claim extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "order_item_id", unique = true)
    private Long orderItemId;

    @Column(name = "option_id")
    private Long optionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", nullable = false)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;

    @Column(name = "reason_code", nullable = false, length = 30)
    private String reasonCode;

    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "pickup_method", nullable = false)
    private ClaimPickupMethod pickupMethod;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "exchange_option", length = 100)
    private String exchangeOption;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Claim(Long memberId,Long orderItemId, Long optionId,
                 ClaimType claimType, String reasonCode,
                 String detail, ClaimPickupMethod pickupMethod, String rejectReason,
                 String exchangeOption, BigDecimal refundAmount, BigDecimal shippingFee) {
        this.memberId = memberId;
        this.orderItemId = orderItemId;
        this.optionId = optionId;
        this.claimType = claimType;
        this.status = ClaimStatus.SUBMITTED;
        this.reasonCode = reasonCode;
        this.detail = detail;
        this.pickupMethod = pickupMethod;
        this.rejectReason = rejectReason;
        this.exchangeOption = exchangeOption;
        this.refundAmount = refundAmount;
        this.shippingFee = shippingFee;

    }

    public void updateStatus(ClaimStatus status) {
        this.status = status;
        if (status == ClaimStatus.SUBMITTED) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void reject(String rejectReason) {
        this.status = ClaimStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    public enum ClaimType {
        EXCHANGE, RETURN
    }

    public enum ClaimStatus {
        SUBMITTED, IN_PROGRESS, COMPLETED, REJECTED
    }

    public enum ClaimPickupMethod {
        COURIER, VISIT
    }


}
