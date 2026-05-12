package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "관리자 상품 목록 응답 DTO")
public class AdminProductListResponseDto {

    @Schema(description = "상품 ID", example = "1")
    private Long productId;

    @Schema(description = "카테고리명", example = "뷰티")
    private String parentCategoryName;

    @Schema(description = "브랜드명", example = "나이키")
    private String brand;

    @Schema(description = "상품명", example = "나이키 에어맥스 97")
    private String productName;

    @Schema(description = "대표 이미지 URL", example = "https://allpick.com/image.jpg")
    private String thumbnailUrl;

    @Schema(description = "판매가격", example = "25000")
    private BigDecimal price;

    @Schema(description = "총 재고 수량", example = "10")
    private Integer stockQuantity;

    @Schema(description = "판매 상태", example = "ON_SALE")
    private Product.Status status;

    @Schema(description = "승인 상태", example = "PENDING")
    private Product.ApprovalStatus approvalStatus;

    @Schema(description = "등록일시")
    private LocalDateTime createdAt;

    public static AdminProductListResponseDto from(Product product) {
        return AdminProductListResponseDto.builder()
                .productId(product.getProductId())
                .parentCategoryName(product.getChildCategory() != null && product.getChildCategory().getParentCategory() != null
                        ? product.getChildCategory().getParentCategory().getCategoryName()
                        : null)
                .brand(product.getBrand())
                .productName(product.getProductName())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .stockQuantity(product.getOptionList().stream()
                        .mapToInt(option -> option.getStockQuantity() == null ? 0 : option.getStockQuantity())
                        .sum())
                .status(product.getStatus())
                .approvalStatus(product.getApprovalStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
