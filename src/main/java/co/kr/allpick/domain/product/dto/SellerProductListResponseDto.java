package co.kr.allpick.domain.product.dto;

import java.math.BigDecimal;

import co.kr.allpick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "판매자 상품 목록 응답 DTO")
public class SellerProductListResponseDto {

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "카테고리명", example = "뷰티")
    private String parentCategoryName;

    @Schema(description = "브랜드명", example = "나이키")
    private String brand;

    @Schema(description = "상품명", example = "나이키 에어맥스 97")
    private String productName;

    @Schema(description = "대표 이미지 URL", example = "https://allpick.com")
    private String thumbnailUrl;

    @Schema(description = "판매가격", example = "25000")
    private BigDecimal price;

    @Schema(description = "상품 승인 상태", example = "PENDING")
    private Product.ApprovalStatus approvalStatus;

    public static SellerProductListResponseDto from(Product product) {
        return SellerProductListResponseDto.builder()
                .productId(product.getProductId())
                .parentCategoryName(product.getParentCategory() != null
                        ? product.getParentCategory().getCategoryName() : null)
                .brand(product.getBrand())
                .productName(product.getProductName())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .approvalStatus(product.getApprovalStatus())
                .build();
    }
}
