package co.kr.allpick.domain.seller.entity;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "sellers")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Seller extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "business_number", nullable = false, unique = true, length = 20)
    private String businessNumber;

    @Column(name = "representative_name", nullable = false, length = 50)
    private String representativeName;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SellerStatus status = SellerStatus.PENDING;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    /**
     * 판매자 승인 상태 변경 메서드
     */
    public void updateStatus(SellerStatus status) {
        this.status = status;
    }

    /**
     * 은행 정보 업데이트 메서드
     */
    public void updateBankInfo(String bankName, String bankAccount) {
        this.bankName = bankName;
        this.bankAccount = bankAccount;
    }
}