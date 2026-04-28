package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ParentCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "부모 카테고리 응답 DTO")
public class ParentCategoryResponseDto {

    @Schema(description = "부모 카테고리 ID", example = "1")
    private Long parentCategoryId;

    @Schema(description = "카테고리 명", example = "뷰티")
    private String categoryName;

    @Schema(description = "사용자가 보는 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "노출 여부", example = "0: 비노출")
    private Integer isActive;

    @Schema(description = "URL 식별자", example = "beauty")
    private String slug;

    public static ParentCategoryResponseDto from(ParentCategory parentCategory) {
        return ParentCategoryResponseDto.builder()
                .parentCategoryId(parentCategory.getParentCategoryId())
                .categoryName(parentCategory.getCategoryName())
                .sortOrder(parentCategory.getSortOrder())
                .isActive(parentCategory.getIsActive())
                .slug(parentCategory.getSlug())
                .build();
    }

}
