package co.kr.allpick.domain.admin.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "inquiry_id")
    private Long inquiryId;

    @Column(name = "claim_id")
    private Long claimId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Builder
    public Attachment(Long inquiryId, Long claimId, TargetType targetType,
                      String imageUrl, int sortOrder) {
        this.inquiryId = inquiryId;
        this.claimId = claimId;
        this.targetType = targetType;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public enum TargetType {
        INQUIRY, CLAIM
    }
}
