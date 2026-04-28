package co.kr.allpick.domain.admin.board.dto;

import co.kr.allpick.domain.admin.board.entity.Faq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "FAQ 응답 DTO")
public class FaqResponseDto {

    @Schema(description = "FAQ ID", example = "1")
    private Long faqId;

    @Schema(description = "관리자 ID", example = "1")
    private Long adminId;

    @Schema(description = "카테고리 (DELIVERY=배송, PAYMENT=결제, CANCEL_REFUND=취소/환불, MEMBER=회원)", example = "DELIVERY")
    private Faq.FaqCategory category;

    @Schema(description = "질문", example = "배송은 얼마나 걸리나요?")
    private String title;

    @Schema(description = "답변", example = "평균 2~3일 소요됩니다.")
    private String content;

    @Schema(description = "노출 순서", example = "1")
    private int displayOrder;

    @Schema(description = "노출 여부", example = "true")
    private boolean isVisible;

    @Schema(description = "등록일시", example = "2026-04-27T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2026-04-27T00:00:00")
    private LocalDateTime updatedAt;

    public static FaqResponseDto from(Faq faq) {
        return FaqResponseDto.builder()
                .faqId(faq.getFaqId())
                .adminId(faq.getAdminId())
                .category(faq.getCategory())
                .title(faq.getTitle())
                .content(faq.getContent())
                .displayOrder(faq.getDisplayOrder())
                .isVisible(faq.isVisible())
                .createdAt(faq.getCreatedAt())
                .updatedAt(faq.getUpdatedAt())
                .build();
    }
}