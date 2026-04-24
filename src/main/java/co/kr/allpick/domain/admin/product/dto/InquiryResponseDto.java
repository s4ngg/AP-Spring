package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "문의 응답 DTO")
public class InquiryResponseDto {

    @Schema(description = "문의 ID", example = "1")
    private Long inquiryId;

    @Schema(description = "회원 ID", example = "5")
    private Long memberId;

    @Schema(description = "주문 상품 ID", example = "10")
    private Long orderItemId;

    @Schema(description = "상품 ID", example = "10")
    private Long productId;

    @Schema(description = "문의 유형", example = "제품")
    private Inquiry.InquiryType inquiryType;

    @Schema(description = "문의 제목", example = "사이즈 문의")
    private String title;

    @Schema(description = "문의 내용", example = "사이즈 문의드립니다.")
    private String content;

    @Schema(description = "문의 상태", example = "접수 대기")
    private Inquiry.InquiryStatus status;

    @Schema(description = "문의 등록 일시", example = "2026-04-22")
    private LocalDateTime createdAt;

    @Schema(description = "답변 목록")
    private List<InquiryAnswerResponseDto> answers;

    public static InquiryResponseDto from(Inquiry inquiry,
                                         List<InquiryAnswerResponseDto> answers) {
        return InquiryResponseDto.builder()
                .inquiryId(inquiry.getInquiryId())
                .memberId(inquiry.getMemberId())
                .orderItemId(inquiry.getOrderItemId())
                .productId(inquiry.getProductId())
                .inquiryType(inquiry.getInquiryType())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .answers(answers)
                .build();
    }


}
