package co.kr.allpick.domain.admin.category.service;

import co.kr.allpick.domain.admin.category.dto.AdminCategoryRequestDto;
import co.kr.allpick.domain.product.dto.ChildCategoryResponseDto;
import co.kr.allpick.domain.product.dto.ParentCategoryResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminCategoryService {

    List<ParentCategoryResponseDto> getParentCategories(AdminJwtUserInfoDto adminInfo);

    ParentCategoryResponseDto createParentCategory(AdminJwtUserInfoDto adminInfo, AdminCategoryRequestDto request);

    ParentCategoryResponseDto updateParentCategory(
            AdminJwtUserInfoDto adminInfo,
            Long parentCategoryId,
            AdminCategoryRequestDto request
    );

    void deleteParentCategory(AdminJwtUserInfoDto adminInfo, Long parentCategoryId);

    List<ChildCategoryResponseDto> getChildCategories(AdminJwtUserInfoDto adminInfo, Long parentCategoryId);

    ChildCategoryResponseDto createChildCategory(
            AdminJwtUserInfoDto adminInfo,
            Long parentCategoryId,
            AdminCategoryRequestDto request
    );

    ChildCategoryResponseDto updateChildCategory(
            AdminJwtUserInfoDto adminInfo,
            Long childCategoryId,
            AdminCategoryRequestDto request
    );

    void deleteChildCategory(AdminJwtUserInfoDto adminInfo, Long childCategoryId);
}
