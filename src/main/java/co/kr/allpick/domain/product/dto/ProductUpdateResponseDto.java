package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;

import co.kr.allpick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "상품수정 응답 DTO")
public class ProductUpdateResponseDto {

    @Schema(description = "수정된 상품 Id", example = "1")
    private Long productId;

    @Schema(description = "상품명", example = "에어맥스 97 화이트")
    private String productName;

    @Schema(description = "브랜드명", example = "나이키")
    private String brand;

    @Schema(description = "썸네일 URL", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "상품 설명", example = "나이키 에어맥스 97 클래식 화이트")
    private String description;

    @Schema(description = "수정된 가격", example = "27000")
    private BigDecimal price;

    @Schema(description = "제조사", example = "나이키 코리아")
    private String manufacturer;

    @Schema(description = "원산지", example = "베트남")
    private String origin;

    @Schema(description = "주의사항", example = "직사광선을 피해 보관하세요.")
    private String precaution;

    public static ProductUpdateResponseDto from(Product product) {
        return ProductUpdateResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .brand(product.getBrand())
                .thumbnailUrl(product.getThumbnailUrl())
                .description(product.getDescription())
                .price(product.getPrice())
                .manufacturer(product.getManufacturer())
                .origin(product.getOrigin())
                .precaution(product.getPrecaution())
                .build();
    }
}
