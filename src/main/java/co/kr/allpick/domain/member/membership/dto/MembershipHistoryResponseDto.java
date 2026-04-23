package co.kr.allpick.domain.member.membership.dto;

import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.membership.entity.MembershipHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "멤버십 등급 변경 이력 응답")
public class MembershipHistoryResponseDto {

    @Schema(description = "이력 PK", example = "1")
    private Long historyId;

    @Schema(description = "이전 등급", example = "NORMAL")
    private MemberGrade previousGrade;

    @Schema(description = "변경 등급", example = "SILVER")
    private MemberGrade newGrade;

    @Schema(description = "해당 월 구매금액", example = "350000")
    private BigDecimal monthlyAmount;

    @Schema(description = "등급 변경 일시", example = "2025-04-01T00:05:00")
    private LocalDateTime changedAt;

    public static MembershipHistoryResponseDto from(MembershipHistory history) {
        return MembershipHistoryResponseDto.builder()
                .historyId(history.getHistoryId())
                .previousGrade(history.getPreviousGrade())
                .newGrade(history.getNewGrade())
                .monthlyAmount(history.getMonthlyAmount())
                .changedAt(history.getChangedAt())
                .build();
    }
}