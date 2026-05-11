package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.entity.ChildCategory;
import co.kr.allpick.domain.product.repository.ChildCategoryRepository;
import co.kr.allpick.domain.product.service.ChildCategoryService;
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
public class ChildCategoryServiceImpl implements ChildCategoryService {

    private static final Logger logger = LogManager.getLogger(ChildCategoryServiceImpl.class);
    private static final int ACTIVE = 1;

    private final ChildCategoryRepository childCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChildCategoryResponseDto> getActiveChildCategories(Long parentCategoryId) {
        logger.info("노출 중인 자식 카테고리 조회 - parentCategoryId: {}", parentCategoryId);
        return childCategoryRepository
                .findByParentCategory_ParentCategoryIdAndIsActiveAndDeletedAtIsNullOrderBySortOrderAsc(parentCategoryId, ACTIVE)
                .stream()
                .map(ChildCategoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChildCategoryResponseDto getChildCategoryBySlug(String slug) {
        logger.info("slug로 자식 카테고리 조회 - slug: {}", slug);
        ChildCategory childCategory = childCategoryRepository.findBySlugAndIsActiveAndDeletedAtIsNull(slug, ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        return ChildCategoryResponseDto.from(childCategory);
    }
}
