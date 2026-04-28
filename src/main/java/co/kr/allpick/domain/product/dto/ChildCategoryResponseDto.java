package co.kr.allpick.domain.product.dto;

import co.kr.allpick.domain.product.entity.ChildCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "자식 카테고리 응답 DTO")
public class ChildCategoryResponseDto {

    @Schema(description = "자식 카테고리 ID", example = "1")
    private Long childCategoryId;

    @Schema(description = "부모 카테고리 ID", example = "1")
    private Long parentCategoryId;

    @Schema(description = "카테고리 명", example = "스킨케어")
    private String categoryName;

    @Schema(description = "사용자가 보는 순서", example = "1")
    private Integer sortOrder;

    @Schema(description = "노출 여부", example = "1")
    private Integer isActive;

    @Schema(description = "URL 식별자", example = "skincare")
    private String slug;

    public static ChildCategoryResponseDto from(ChildCategory childCategory) {
        return ChildCategoryResponseDto.builder()
                .childCategoryId(childCategory.getChildCategoryId())
                .parentCategoryId(childCategory.getParentCategory() != null
                        ? childCategory.getParentCategory().getParentCategoryId()
                        : null)
                .categoryName(childCategory.getCategoryName())
                .sortOrder(childCategory.getSortOrder())
                .isActive(childCategory.getIsActive())
                .slug(childCategory.getSlug())
                .build();
    }
}