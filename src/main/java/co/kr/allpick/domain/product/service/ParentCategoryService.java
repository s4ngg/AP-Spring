package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;

import java.util.List;

public interface ParentCategoryService {

    // 노출 중인 전체 부모 카테고리 조회
    List<ParentCategoryResponseDto> getActiveParentCategories();

    // slug로 단건 조회
    ParentCategoryResponseDto getParentCategoryBySlug(String slug);

}
