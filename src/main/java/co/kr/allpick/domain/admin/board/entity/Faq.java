package co.kr.allpick.domain.admin.board.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "faqs")
@Getter
@NoArgsConstructor
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long faqId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FaqCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_visible", nullable = false)
    private boolean isVisible;


    @Builder
    public Faq(Long adminId, FaqCategory category, String title,
               String content, int displayOrder) {
        this.adminId = adminId;
        this.category = category;
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
        this.isVisible = true;
    }

    public void update(FaqCategory category, String title,
                       String content, int displayOrder) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.displayOrder = displayOrder;
    }

    public void toggleVisibility(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public enum  FaqCategory {
        DELIVERY, PAYMENT, CANCEL_REFUND, MEMBER
    }

}
