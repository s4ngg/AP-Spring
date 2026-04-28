package co.kr.allpick.domain.seller.product.dto;

import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 응답 DTO")
public class SellerProductResponseDto {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "수분 세럼 30ml")
    private String name;

    @Schema(description = "카테고리", example = "BEAUTY")
    private SellerProduct.Category category;

    @Schema(description = "판매가", example = "45000")
    private Integer price;

    @Schema(description = "재고 수량", example = "120")
    private Integer stock;

    @Schema(description = "상품 간략 설명")
    private String shortDescription;

    @Schema(description = "대표 이미지 URL")
    private String thumbnailUrl;

    @Schema(description = "상품 상태", example = "ON_SALE")
    private SellerProduct.Status status;

    public static SellerProductResponseDto of(SellerProduct product) {
        return SellerProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .shortDescription(product.getShortDescription())
                .thumbnailUrl(product.getThumbnailUrl())
                .status(product.getStatus())
                .build();
    }
}