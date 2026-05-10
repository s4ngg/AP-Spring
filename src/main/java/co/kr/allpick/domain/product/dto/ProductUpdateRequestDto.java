package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "상품수정 요청 DTO - 전달된 필드만 수정됨 (null이면 기존값 유지)")
public class ProductUpdateRequestDto {

    @PositiveOrZero
    @Schema(description = "수정 가격", example = "27000")
    private BigDecimal price;

    @Schema(description = "상품명", example = "에어맥스 97 화이트")
    private String productName;

    @Schema(description = "브랜드명", example = "나이키")
    private String brand;

    @Schema(description = "썸네일 URL", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "상품 설명", example = "나이키 에어맥스 97 클래식 화이트")
    private String description;

    @Schema(description = "제조사", example = "나이키 코리아")
    private String manufacturer;

    @Schema(description = "원산지", example = "베트남")
    private String origin;

    @Schema(description = "주의사항", example = "직사광선을 피해 보관하세요.")
    private String precaution;

    @Schema(description = "상품 옵션 리스트 (전체 교체)")
    private List<ProductOptionRequestDto> optionList;

    @Schema(description = "상품 이미지 리스트 (전체 교체)")
    private List<ProductImageRequestDto> productImageList;
}
