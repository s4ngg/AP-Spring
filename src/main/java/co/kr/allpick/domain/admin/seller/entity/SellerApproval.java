package co.kr.allpick.domain.admin.seller.entity;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.global.common.BaseEntity;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_approvals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_approval_id")
    private Long sellerApprovalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 20)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApprovalStatus status;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder
    private SellerApproval(Seller seller, Admin admin, RequestType requestType,
                           ApprovalStatus status, String rejectReason, LocalDateTime processedAt) {
        this.seller = seller;
        this.admin = admin;
        this.requestType = requestType;
        this.status = status;
        this.rejectReason = rejectReason;
        this.processedAt = processedAt;
    }

    public static SellerApproval approved(Seller seller, Admin admin, RequestType requestType) {
        return SellerApproval.builder()
                .seller(seller)
                .admin(admin)
                .requestType(requestType)
                .status(ApprovalStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static SellerApproval rejected(Seller seller, Admin admin, RequestType requestType, String rejectReason) {
        return SellerApproval.builder()
                .seller(seller)
                .admin(admin)
                .requestType(requestType)
                .status(ApprovalStatus.REJECTED)
                .rejectReason(rejectReason)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public enum RequestType {
        REGISTER, SUSPEND
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
