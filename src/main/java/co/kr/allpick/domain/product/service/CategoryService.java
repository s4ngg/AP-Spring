package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;

import java.util.List;

public interface CategoryService {

    List<CategoryProductResponseDto> getProductsByCategory(Long categoryId);
}