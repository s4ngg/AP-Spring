package co.kr.allpick.domain.admin.category.service.impl;

import co.kr.allpick.domain.admin.category.dto.AdminCategoryRequestDto;
import co.kr.allpick.domain.admin.category.service.AdminCategoryService;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.domain.product.entity.ChildCategory;
import co.kr.allpick.domain.product.entity.ParentCategory;
import co.kr.allpick.domain.product.repository.ChildCategoryRepository;
import co.kr.allpick.domain.product.repository.ParentCategoryRepository;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private static final Logger logger = LogManager.getLogger(AdminCategoryServiceImpl.class);

    private final AdminRepository adminRepository;
    private final ParentCategoryRepository parentCategoryRepository;
    private final ChildCategoryRepository childCategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParentCategoryResponseDto> getParentCategories(AdminJwtUserInfoDto adminInfo) {
        Admin admin = getSuperAdmin(adminInfo);
        logger.info("[AdminCategoryServiceImpl] 대분류 목록 조회 - actorAdminId: {}", admin.getAdminId());

        return parentCategoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAsc()
                .stream()
                .map(ParentCategoryResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public ParentCategoryResponseDto createParentCategory(AdminJwtUserInfoDto adminInfo, AdminCategoryRequestDto request) {
        Admin admin = getSuperAdmin(adminInfo);
        validateSlugForCreate(request.getSlug());

        ParentCategory parentCategory = parentCategoryRepository.save(ParentCategory.builder()
                .categoryName(request.getCategoryName().trim())
                .slug(request.getSlug().trim())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive())
                .build());

        logger.info("[AdminCategoryServiceImpl] 대분류 등록 완료 - actorAdminId: {}, parentCategoryId: {}",
                admin.getAdminId(), parentCategory.getParentCategoryId());
        return ParentCategoryResponseDto.from(parentCategory);
    }

    @Override
    @Transactional
    public ParentCategoryResponseDto updateParentCategory(
            AdminJwtUserInfoDto adminInfo,
            Long parentCategoryId,
            AdminCategoryRequestDto request
    ) {
        Admin admin = getSuperAdmin(adminInfo);
        ParentCategory parentCategory = getParentCategory(parentCategoryId);
        validateSlugForUpdate(request.getSlug(), parentCategory.getSlug());

        parentCategory.update(
                request.getCategoryName().trim(),
                request.getSlug().trim(),
                request.getSortOrder(),
                request.getIsActive()
        );

        logger.info("[AdminCategoryServiceImpl] 대분류 수정 완료 - actorAdminId: {}, parentCategoryId: {}",
                admin.getAdminId(), parentCategoryId);
        return ParentCategoryResponseDto.from(parentCategory);
    }

    @Override
    @Transactional
    public void deleteParentCategory(AdminJwtUserInfoDto adminInfo, Long parentCategoryId) {
        Admin admin = getSuperAdmin(adminInfo);
        ParentCategory parentCategory = getParentCategory(parentCategoryId);
        parentCategory.deactivate();

        logger.info("[AdminCategoryServiceImpl] 대분류 비활성화 완료 - actorAdminId: {}, parentCategoryId: {}",
                admin.getAdminId(), parentCategoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChildCategoryResponseDto> getChildCategories(AdminJwtUserInfoDto adminInfo, Long parentCategoryId) {
        Admin admin = getSuperAdmin(adminInfo);
        getParentCategory(parentCategoryId);
        logger.info("[AdminCategoryServiceImpl] 소분류 목록 조회 - actorAdminId: {}, parentCategoryId: {}",
                admin.getAdminId(), parentCategoryId);

        return childCategoryRepository
                .findByParentCategory_ParentCategoryIdAndDeletedAtIsNullOrderBySortOrderAsc(parentCategoryId)
                .stream()
                .map(ChildCategoryResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public ChildCategoryResponseDto createChildCategory(
            AdminJwtUserInfoDto adminInfo,
            Long parentCategoryId,
            AdminCategoryRequestDto request
    ) {
        Admin admin = getSuperAdmin(adminInfo);
        ParentCategory parentCategory = getParentCategory(parentCategoryId);
        validateSlugForCreate(request.getSlug());

        ChildCategory childCategory = childCategoryRepository.save(ChildCategory.builder()
                .parentCategory(parentCategory)
                .categoryName(request.getCategoryName().trim())
                .slug(request.getSlug().trim())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive())
                .build());

        logger.info("[AdminCategoryServiceImpl] 소분류 등록 완료 - actorAdminId: {}, parentCategoryId: {}, childCategoryId: {}",
                admin.getAdminId(), parentCategoryId, childCategory.getChildCategoryId());
        return ChildCategoryResponseDto.from(childCategory);
    }

    @Override
    @Transactional
    public ChildCategoryResponseDto updateChildCategory(
            AdminJwtUserInfoDto adminInfo,
            Long childCategoryId,
            AdminCategoryRequestDto request
    ) {
        Admin admin = getSuperAdmin(adminInfo);
        ChildCategory childCategory = getChildCategory(childCategoryId);
        ParentCategory parentCategory = childCategory.getParentCategory();
        validateSlugForUpdate(request.getSlug(), childCategory.getSlug());

        childCategory.update(
                parentCategory,
                request.getCategoryName().trim(),
                request.getSlug().trim(),
                request.getSortOrder(),
                request.getIsActive()
        );

        logger.info("[AdminCategoryServiceImpl] 소분류 수정 완료 - actorAdminId: {}, childCategoryId: {}",
                admin.getAdminId(), childCategoryId);
        return ChildCategoryResponseDto.from(childCategory);
    }

    @Override
    @Transactional
    public void deleteChildCategory(AdminJwtUserInfoDto adminInfo, Long childCategoryId) {
        Admin admin = getSuperAdmin(adminInfo);
        ChildCategory childCategory = getChildCategory(childCategoryId);
        childCategory.deactivate();

        logger.info("[AdminCategoryServiceImpl] 소분류 비활성화 완료 - actorAdminId: {}, childCategoryId: {}",
                admin.getAdminId(), childCategoryId);
    }

    private Admin getSuperAdmin(AdminJwtUserInfoDto adminInfo) {
        if (adminInfo == null || adminInfo.getRole() != Admin.AdminRole.SUPER_ADMIN) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }

        Admin admin = adminRepository.findById(adminInfo.getAdminId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        if (admin.getRole() != Admin.AdminRole.SUPER_ADMIN || admin.getStatus() != Admin.AdminStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }
        return admin;
    }

    private ParentCategory getParentCategory(Long parentCategoryId) {
        return parentCategoryRepository.findByParentCategoryIdAndDeletedAtIsNull(parentCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private ChildCategory getChildCategory(Long childCategoryId) {
        return childCategoryRepository.findByChildCategoryIdAndDeletedAtIsNull(childCategoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private void validateSlugForCreate(String slug) {
        if (isSlugDuplicated(slug)) {
            throw new BusinessException(ErrorCode.CATEGORY_SLUG_DUPLICATED);
        }
    }

    private void validateSlugForUpdate(String requestSlug, String currentSlug) {
        String trimmedSlug = requestSlug.trim();
        if (!trimmedSlug.equals(currentSlug) && isSlugDuplicated(trimmedSlug)) {
            throw new BusinessException(ErrorCode.CATEGORY_SLUG_DUPLICATED);
        }
    }

    private boolean isSlugDuplicated(String slug) {
        return parentCategoryRepository.existsBySlugAndDeletedAtIsNull(slug.trim())
                || childCategoryRepository.existsBySlugAndDeletedAtIsNull(slug.trim());
    }
}
