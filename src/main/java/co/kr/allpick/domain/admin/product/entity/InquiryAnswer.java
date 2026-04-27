package co.kr.allpick.domain.admin.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiry_answers")
@Getter
@NoArgsConstructor
public class InquiryAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_answers_id")
    private Long inquiryAnswersId;

    @Column(name = "inquiry_id", nullable = false)
    private Long inquiryId;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder
    public InquiryAnswer(Long inquiryId, Long adminId, Long sellerId, String content) {
        this.inquiryId = inquiryId;
        this.adminId = adminId;
        this.sellerId = sellerId;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
