package co.kr.allpick.domain.seller.product.dto;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.product.entity.SellerProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 등록/수정 요청 DTO")
public class SellerProductRequestDto {

    @Schema(description = "상품명", example = "수분 세럼 30ml", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Schema(description = "카테고리", example = "BEAUTY", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "카테고리는 필수입니다.")
    private SellerProduct.Category category;

    @Schema(description = "판매가", example = "45000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "판매가는 필수입니다.")
    @Min(value = 0, message = "판매가는 0원 이상이어야 합니다.")
    private Integer price;

    @Schema(description = "재고 수량", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "재고 수량은 필수입니다.")
    @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
    private Integer stock;

    @Schema(description = "상품 간략 설명", example = "촉촉하고 건강한 피부를 위한 수분 세럼입니다.")
    private String shortDescription;

    @Schema(description = "대표 이미지 URL", example = "https://...")
    private String thumbnailUrl;

    @Schema(description = "상품 상세 설명", example = "상품 상세 설명입니다.")
    private String description;

    public SellerProduct toEntity(Seller seller) {
        return SellerProduct.builder()
                .seller(seller)
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stock(this.stock)
                .shortDescription(this.shortDescription)
                .thumbnailUrl(this.thumbnailUrl)
                .description(this.description)
                .build();
    }
}