package co.kr.allpick.domain.admin.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "product_id")
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", nullable = false)
    private InquiryType inquiryType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InquiryStatus status;

    @Builder
    public Inquiry(Long memberId, Long orderItemId, Long productId, InquiryType inquiryType,
                   String title, String content) {
        this.memberId = memberId;
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.inquiryType = inquiryType;
        this.title = title;
        this.content = content;
        this.status = InquiryStatus.PENDING;

    }

    public void updateStatus(InquiryStatus status) {
        this.status = status;
    }

    public enum InquiryType {
        PRODUCT, DELIVERY, PAYMENT, ETC
    }
    public enum InquiryStatus {
        PENDING, PROCESSING, COMPLETED
    }

}
