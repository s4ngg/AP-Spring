package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;

import java.util.List;

public interface ChildCategoryService {

    // 특정 부모 카테고리의 노출 중인 자식 카테고리 조회
    List<ChildCategoryResponseDto> getActiveChildCategories(Long parentCategoryId);

    // slug로 단건 조회
    ChildCategoryResponseDto getChildCategoryBySlug(String slug);
}