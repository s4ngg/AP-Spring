package co.kr.allpick.domain.admin.product.dto;


import co.kr.allpick.domain.admin.product.entity.Inquiry;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 등록 요청 DTO")
public class InquiryCreateRequestDto {

    @Schema(description = "주문 상품 ID", example = "10")
    private Long orderItemId;

    @Schema(description = "상품 ID", example = "10")
    private Long productId;

    @NotNull
    @Schema(description = "문의 유형", example = "PRODUCT")
    private Inquiry.InquiryType inquiryType;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "문의 제목", example = "상품 사이즈 문의드립니다.")
    private String title;

    @NotBlank
    @Schema(description = "문의 내용", example = "정 사이즈인지 궁금합니다.")
    private String content;

    public Inquiry toEntity(Long memberId) {
        return Inquiry.builder()
                .memberId(memberId)
                .orderItemId(this.orderItemId)
                .productId(this.productId)
                .inquiryType(this.inquiryType)
                .title(this.title)
                .content(this.content)
                .build();
    }


}
