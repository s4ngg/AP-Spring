package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
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
class ParentCategoryServiceImplTest {

    @Mock
    ParentCategoryRepository parentCategoryRepository;

    @InjectMocks
    ParentCategoryServiceImpl parentCategoryService;

    @Test
    @DisplayName("노출 중인 부모 카테고리 전체 조회 성공")
    void 노출_중인_부모_카테고리_전체_조회_성공() {
        // given
        ParentCategory category1 = ParentCategory.builder()
                .categoryName("뷰티")
                .sortOrder(1)
                .isActive(1)
                .slug("beauty")
                .build();

        ParentCategory category2 = ParentCategory.builder()
                .categoryName("패션")
                .sortOrder(2)
                .isActive(1)
                .slug("fashion")
                .build();

        given(parentCategoryRepository.findByIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(1))
                .willReturn(List.of(category1, category2));

        // when
        List<ParentCategoryResponseDto> result = parentCategoryService.getActiveParentCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("뷰티");
        assertThat(result.get(0).getSlug()).isEqualTo("beauty");
        assertThat(result.get(1).getCategoryName()).isEqualTo("패션");
        assertThat(result.get(1).getSlug()).isEqualTo("fashion");
    }

    @Test
    @DisplayName("노출 중인 부모 카테고리 없음")
    void 노출_중인_부모_카테고리_없음() {
        // given
        given(parentCategoryRepository.findByIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(1))
                .willReturn(List.of());

        // when
        List<ParentCategoryResponseDto> result = parentCategoryService.getActiveParentCategories();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("slug로 부모 카테고리 단건 조회 성공")
    void slug로_부모_카테고리_단건_조회_성공() {
        // given
        ParentCategory category = ParentCategory.builder()
                .categoryName("뷰티")
                .sortOrder(1)
                .isActive(1)
                .slug("beauty")
                .build();

        given(parentCategoryRepository.findBySlugAndIsActiveAndDeletedAtIsNull("beauty", 1))
                .willReturn(Optional.of(category));

        // when
        ParentCategoryResponseDto result = parentCategoryService.getParentCategoryBySlug("beauty");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("뷰티");
        assertThat(result.getSlug()).isEqualTo("beauty");
    }

    @Test
    @DisplayName("slug로 부모 카테고리 단건 조회 실패 - 존재하지 않는 slug")
    void slug로_부모_카테고리_단건_조회_실패() {
        // given
        given(parentCategoryRepository.findBySlugAndIsActiveAndDeletedAtIsNull("없는slug", 1))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> parentCategoryService.getParentCategoryBySlug("없는slug"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }
}
