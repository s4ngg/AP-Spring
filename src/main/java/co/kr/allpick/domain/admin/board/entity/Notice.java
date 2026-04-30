package co.kr.allpick.domain.admin.board.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notices")
@Getter
@NoArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "is_fixed", nullable = false)
    private boolean fixed;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Builder
    public Notice(Long adminId, String title, String content, boolean fixed, String imageUrl) {
        this.adminId = adminId;
        this.title = title;
        this.content = content;
        this.fixed = fixed;
        this.imageUrl = imageUrl;
    }

    public void update(String title, String content, boolean fixed, String imageUrl) {
        this.title = title;
        this.content = content;
        this.fixed = fixed;
        this.imageUrl = imageUrl;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
