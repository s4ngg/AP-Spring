package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.domain.product.service.ParentCategoryService;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentCategoryServiceImpl implements ParentCategoryService {

    private static final Logger logger = LogManager.getLogger(ParentCategoryServiceImpl.class);

    private final ParentCategoryRepository parentCategoryRepository;
    private static final int ACTIVE = 1;

    @Override
    @Transactional(readOnly = true)
    public List<ParentCategoryResponseDto> getActiveParentCategories() {
        logger.info("노출 중인 부모 카테고리 전체 조회");
        return parentCategoryRepository.findByIsActiveOrderBySortOrderAsc(ACTIVE)
                .stream()
                .map(ParentCategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ParentCategoryResponseDto getParentCategoryBySlug(String slug) {
        logger.info("slug로 부모 카테고리 조회 - slug: {}", slug);
        ParentCategory parentCategory = parentCategoryRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return ParentCategoryResponseDto.from(parentCategory);
    }
}
