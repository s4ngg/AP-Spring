package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.InquiryAnswer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "답변 응답 DTO")
public class InquiryAnswerResponseDto {

    @Schema(description = "답변 ID", example = "1")
    private Long inquiryAnswerId;

    @Schema(description = "문의 ID", example = "10")
    private Long inquiryId;

    @Schema(description = "관리자 ID", example = "2")
    private Long adminId;

    @Schema(description = "판매자 ID", example = "13")
    private Long sellerId;

    @Schema(description = "답변 등록", example = "정 사이즈 입니다.")
    private String content;

    @Schema(description = "답변 등록 일시", example = "2026-04-22")
    private LocalDateTime createdAt;

    public static InquiryAnswerResponseDto from(InquiryAnswer answer) {
        return InquiryAnswerResponseDto.builder()
                .inquiryAnswerId(answer.getInquiryAnswersId())
                .inquiryId(answer.getInquiryId())
                .adminId(answer.getAdminId())
                .sellerId(answer.getSellerId())
                .content(answer.getContent())
                .createdAt(answer.getCreatedAt())
                .build();
    }

}
