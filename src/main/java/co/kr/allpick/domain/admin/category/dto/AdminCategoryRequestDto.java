package co.kr.allpick.domain.admin.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 카테고리 요청 DTO")
public class AdminCategoryRequestDto {

    @NotBlank(message = "카테고리명은 필수입니다.")
    @Schema(description = "카테고리명", example = "뷰티")
    private String categoryName;

    @NotBlank(message = "slug는 필수입니다.")
    @Schema(description = "URL 식별자", example = "beauty")
    private String slug;

    @NotNull(message = "정렬 순서는 필수입니다.")
    @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
    @Schema(description = "정렬 순서", example = "1")
    private Integer sortOrder;

    @NotNull(message = "노출 여부는 필수입니다.")
    @Min(value = 0, message = "노출 여부는 0 또는 1이어야 합니다.")
    @Max(value = 1, message = "노출 여부는 0 또는 1이어야 합니다.")
    @Schema(description = "노출 여부", example = "1")
    private Integer isActive;
}
