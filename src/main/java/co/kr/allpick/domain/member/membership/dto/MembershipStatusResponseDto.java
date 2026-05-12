package co.kr.allpick.domain.member.membership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "멤버십 현황 응답 DTO")
public class MembershipStatusResponseDto {

    @Schema(description = "현재 등급", example = "NORMAL")
    private String currentGrade;

    @Schema(description = "이번 달 구매 금액", example = "150000")
    private long thisMonthAmount;

    @Schema(description = "다음 달 예상 등급", example = "SILVER")
    private String predictedGrade;

    @Schema(description = "다음 등급", example = "SILVER")
    private String nextGrade;

    @Schema(description = "다음 등급까지 필요 금액", example = "150000")
    private long amountToNextGrade;
}