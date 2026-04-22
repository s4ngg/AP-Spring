package co.kr.allpick.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "카테고리별 상품 응답 DTO")
public class CategoryProductResponseDto {

    @Schema(description = "상품 고유 ID", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "모던 침대 프레임")
    private String productName;

    @Schema(description = "상품 가격", example = "329000")
    private int price;

    @Schema(description = "상품 썸네일 이미지 URL", example = "https://via.placeholder.com/300")
    private String thumbnailUrl;
}