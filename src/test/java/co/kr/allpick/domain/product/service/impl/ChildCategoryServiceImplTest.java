package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.entity.ChildCategory;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.repository.ChildCategoryRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChildCategoryServiceImplTest {

    @Mock
    ChildCategoryRepository childCategoryRepository;

    @InjectMocks
    ChildCategoryServiceImpl childCategoryService;

    @Test
    @DisplayName("노출 중인 자식 카테고리 목록 조회 성공")
    void 노출_중인_자식_카테고리_목록_조회_성공() {
        // given
        Long parentCategoryId = 1L;

        ParentCategory parentCategory = ParentCategory.builder()
                .categoryName("뷰티")
                .sortOrder(1)
                .isActive(1)
                .slug("beauty")
                .build();

        ChildCategory child1 = ChildCategory.builder()
                .parentCategory(parentCategory)
                .categoryName("스킨케어")
                .sortOrder(1)
                .isActive(1)
                .slug("skincare")
                .build();

        ChildCategory child2 = ChildCategory.builder()
                .parentCategory(parentCategory)
                .categoryName("메이크업")
                .sortOrder(2)
                .isActive(1)
                .slug("makeup")
                .build();

        given(childCategoryRepository
                .findByParentCategory_ParentCategoryIdAndIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(parentCategoryId, 1))
                .willReturn(List.of(child1, child2));

        // when
        List<ChildCategoryResponseDto> result = childCategoryService.getActiveChildCategories(parentCategoryId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("스킨케어");
        assertThat(result.get(0).getSlug()).isEqualTo("skincare");
        assertThat(result.get(1).getCategoryName()).isEqualTo("메이크업");
        assertThat(result.get(1).getSlug()).isEqualTo("makeup");
    }

    @Test
    @DisplayName("노출 중인 자식 카테고리 없음")
    void 노출_중인_자식_카테고리_없음() {
        // given
        Long parentCategoryId = 99L;

        given(childCategoryRepository
                .findByParentCategory_ParentCategoryIdAndIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(parentCategoryId, 1))
                .willReturn(List.of());

        // when
        List<ChildCategoryResponseDto> result = childCategoryService.getActiveChildCategories(parentCategoryId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("slug로 자식 카테고리 단건 조회 성공")
    void slug로_자식_카테고리_단건_조회_성공() {
        // given
        ParentCategory parentCategory = ParentCategory.builder()
                .categoryName("뷰티")
                .sortOrder(1)
                .isActive(1)
                .slug("beauty")
                .build();

        ChildCategory childCategory = ChildCategory.builder()
                .parentCategory(parentCategory)
                .categoryName("스킨케어")
                .sortOrder(1)
                .isActive(1)
                .slug("skincare")
                .build();

        given(childCategoryRepository.findBySlugAndIsActiveAndDeletedAtIsNull("skincare", 1))
                .willReturn(Optional.of(childCategory));

        // when
        ChildCategoryResponseDto result = childCategoryService.getChildCategoryBySlug("skincare");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("스킨케어");
        assertThat(result.getSlug()).isEqualTo("skincare");
    }

    @Test
    @DisplayName("slug로 자식 카테고리 단건 조회 실패 - 존재하지 않는 slug")
    void slug로_자식_카테고리_단건_조회_실패() {
        // given
        given(childCategoryRepository.findBySlugAndIsActiveAndDeletedAtIsNull("없는slug", 1))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> childCategoryService.getChildCategoryBySlug("없는slug"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }
}
