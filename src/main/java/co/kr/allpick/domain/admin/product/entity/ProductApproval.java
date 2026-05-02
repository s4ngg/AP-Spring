package co.kr.allpick.domain.admin.product.entity;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.product.entity.Product;
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
@Table(name = "product_approvals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductApproval extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_approval_id")
    private Long productApprovalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

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
    private ProductApproval(Product product, Admin admin, RequestType requestType,
                            ApprovalStatus status, String rejectReason, LocalDateTime processedAt) {
        this.product = product;
        this.admin = admin;
        this.requestType = requestType;
        this.status = status;
        this.rejectReason = rejectReason;
        this.processedAt = processedAt;
    }

    public static ProductApproval approved(Product product, Admin admin, RequestType requestType) {
        return ProductApproval.builder()
                .product(product)
                .admin(admin)
                .requestType(requestType)
                .status(ApprovalStatus.APPROVED)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static ProductApproval rejected(Product product, Admin admin, RequestType requestType, String rejectReason) {
        return ProductApproval.builder()
                .product(product)
                .admin(admin)
                .requestType(requestType)
                .status(ApprovalStatus.REJECTED)
                .rejectReason(rejectReason)
                .processedAt(LocalDateTime.now())
                .build();
    }

    public enum RequestType {
        REGISTER, UPDATE, DELETE
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
