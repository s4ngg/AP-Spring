package co.kr.allpick.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "term_type", nullable = false, unique = true)
    private TermsType termsType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public enum TermsType {
        TERMS_OF_USE,    // 이용약관
        PRIVACY_POLICY,  // 개인정보처리방침
        MARKETING,       // 마케팅
        LOCATION         // 위치
    }
}