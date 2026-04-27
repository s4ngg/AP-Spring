package co.kr.allpick.domain.member.membership.entity;

import co.kr.allpick.domain.member.entity.MemberGrade;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MembershipHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_grade", nullable = false)
    private MemberGrade previousGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_grade", nullable = false)
    private MemberGrade newGrade;

    @Column(name = "monthly_amount", nullable = false, precision = 15, scale = 0)
    private BigDecimal monthlyAmount;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public static MembershipHistory of(Long memberId,
                                       MemberGrade previousGrade,
                                       MemberGrade newGrade,
                                       BigDecimal monthlyAmount) {
        return MembershipHistory.builder()
                .memberId(memberId)
                .previousGrade(previousGrade)
                .newGrade(newGrade)
                .monthlyAmount(monthlyAmount)
                .changedAt(LocalDateTime.now())
                .build();
    }
}