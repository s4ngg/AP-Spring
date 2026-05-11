package co.kr.allpick.domain.admin.category.service.impl;

import co.kr.allpick.domain.admin.category.dto.AdminCategoryRequestDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceImplTest {

    @Mock
    AdminRepository adminRepository;

    @Mock
    ParentCategoryRepository parentCategoryRepository;

    @Mock
    ChildCategoryRepository childCategoryRepository;

    @InjectMocks
    AdminCategoryServiceImpl adminCategoryService;

    @Test
    @DisplayName("대분류 목록 조회 성공")
    void 대분류_목록_조회_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ParentCategory beauty = parentCategory(1L, "뷰티", "beauty", 1, 1);
        ParentCategory fashion = parentCategory(2L, "패션", "fashion", 2, 0);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAsc())
                .willReturn(List.of(beauty, fashion));

        // when
        List<ParentCategoryResponseDto> result = adminCategoryService.getParentCategories(adminInfo);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCategoryName()).isEqualTo("뷰티");
        assertThat(result.get(1).getIsActive()).isZero();
    }

    @Test
    @DisplayName("대분류 등록 성공")
    void 대분류_등록_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        AdminCategoryRequestDto request = request("뷰티", "beauty", 1, 1);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.existsBySlugAndDeletedAtIsNull("beauty")).willReturn(false);
        given(childCategoryRepository.existsBySlugAndDeletedAtIsNull("beauty")).willReturn(false);
        given(parentCategoryRepository.save(any(ParentCategory.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ParentCategoryResponseDto result = adminCategoryService.createParentCategory(adminInfo, request);

        // then
        assertThat(result.getCategoryName()).isEqualTo("뷰티");
        assertThat(result.getSlug()).isEqualTo("beauty");

        ArgumentCaptor<ParentCategory> captor = ArgumentCaptor.forClass(ParentCategory.class);
        verify(parentCategoryRepository).save(captor.capture());
        assertThat(captor.getValue().getCategoryName()).isEqualTo("뷰티");
        assertThat(captor.getValue().getSlug()).isEqualTo("beauty");
        assertThat(captor.getValue().getSortOrder()).isEqualTo(1);
        assertThat(captor.getValue().getIsActive()).isEqualTo(1);
    }

    @Test
    @DisplayName("대분류 등록 실패 - slug 중복")
    void 대분류_등록_실패_slug중복() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        AdminCategoryRequestDto request = request("뷰티", "beauty", 1, 1);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.existsBySlugAndDeletedAtIsNull("beauty")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminCategoryService.createParentCategory(adminInfo, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
        verify(parentCategoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("대분류 수정 성공")
    void 대분류_수정_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ParentCategory parentCategory = parentCategory(1L, "뷰티", "beauty", 1, 1);
        AdminCategoryRequestDto request = request("뷰티관", "beauty-zone", 2, 0);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.findByParentCategoryIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(parentCategory));
        given(parentCategoryRepository.existsBySlugAndDeletedAtIsNull("beauty-zone")).willReturn(false);
        given(childCategoryRepository.existsBySlugAndDeletedAtIsNull("beauty-zone")).willReturn(false);

        // when
        ParentCategoryResponseDto result = adminCategoryService.updateParentCategory(adminInfo, 1L, request);

        // then
        assertThat(result.getCategoryName()).isEqualTo("뷰티관");
        assertThat(parentCategory.getSlug()).isEqualTo("beauty-zone");
        assertThat(parentCategory.getSortOrder()).isEqualTo(2);
        assertThat(parentCategory.getIsActive()).isZero();
    }

    @Test
    @DisplayName("대분류 삭제 성공 - soft delete")
    void 대분류_삭제_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ParentCategory parentCategory = parentCategory(1L, "뷰티", "beauty", 1, 1);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.findByParentCategoryIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(parentCategory));

        // when
        adminCategoryService.deleteParentCategory(adminInfo, 1L);

        // then
        assertThat(parentCategory.getIsActive()).isZero();
        assertThat(parentCategory.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("소분류 등록 성공")
    void 소분류_등록_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ParentCategory parentCategory = parentCategory(1L, "뷰티", "beauty", 1, 1);
        AdminCategoryRequestDto request = request("스킨케어", "skincare", 1, 1);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.findByParentCategoryIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(parentCategory));
        given(parentCategoryRepository.existsBySlugAndDeletedAtIsNull("skincare")).willReturn(false);
        given(childCategoryRepository.existsBySlugAndDeletedAtIsNull("skincare")).willReturn(false);
        given(childCategoryRepository.save(any(ChildCategory.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ChildCategoryResponseDto result = adminCategoryService.createChildCategory(adminInfo, 1L, request);

        // then
        assertThat(result.getCategoryName()).isEqualTo("스킨케어");

        ArgumentCaptor<ChildCategory> captor = ArgumentCaptor.forClass(ChildCategory.class);
        verify(childCategoryRepository).save(captor.capture());
        assertThat(captor.getValue().getParentCategory()).isEqualTo(parentCategory);
        assertThat(captor.getValue().getCategoryName()).isEqualTo("스킨케어");
        assertThat(captor.getValue().getSlug()).isEqualTo("skincare");
    }

    @Test
    @DisplayName("소분류 등록 실패 - 부모 카테고리 없음")
    void 소분류_등록_실패_부모카테고리없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        AdminCategoryRequestDto request = request("스킨케어", "skincare", 1, 1);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(parentCategoryRepository.findByParentCategoryIdAndDeletedAtIsNull(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCategoryService.createChildCategory(adminInfo, 999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
        verify(childCategoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("소분류 수정 성공")
    void 소분류_수정_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ParentCategory parentCategory = parentCategory(1L, "뷰티", "beauty", 1, 1);
        ChildCategory childCategory = childCategory(1L, parentCategory, "스킨케어", "skincare", 1, 1);
        AdminCategoryRequestDto request = request("기초화장품", "basic-cosmetic", 2, 0);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(childCategoryRepository.findByChildCategoryIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(childCategory));
        given(parentCategoryRepository.existsBySlugAndDeletedAtIsNull("basic-cosmetic")).willReturn(false);
        given(childCategoryRepository.existsBySlugAndDeletedAtIsNull("basic-cosmetic")).willReturn(false);

        // when
        ChildCategoryResponseDto result = adminCategoryService.updateChildCategory(adminInfo, 1L, request);

        // then
        assertThat(result.getCategoryName()).isEqualTo("기초화장품");
        assertThat(childCategory.getParentCategory()).isEqualTo(parentCategory);
        assertThat(childCategory.getSlug()).isEqualTo("basic-cosmetic");
        assertThat(childCategory.getIsActive()).isZero();
    }

    @Test
    @DisplayName("소분류 삭제 실패 - 카테고리 없음")
    void 소분류_삭제_실패_카테고리없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(childCategoryRepository.findByChildCategoryIdAndDeletedAtIsNull(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCategoryService.deleteChildCategory(adminInfo, 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    @DisplayName("카테고리 관리 실패 - 인증 관리자 정보 없음")
    void 카테고리_관리_실패_관리자정보없음() {
        // when & then
        assertThatThrownBy(() -> adminCategoryService.getParentCategories(null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(parentCategoryRepository, never()).findAllByDeletedAtIsNullOrderBySortOrderAsc();
    }

    @Test
    @DisplayName("카테고리 관리 실패 - JWT 권한 없음")
    void 카테고리_관리_실패_JWT권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = csAdminInfo();

        // when & then
        assertThatThrownBy(() -> adminCategoryService.createParentCategory(adminInfo, request("뷰티", "beauty", 1, 1)))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(parentCategoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("카테고리 관리 실패 - 관리자 없음")
    void 카테고리_관리_실패_관리자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCategoryService.getParentCategories(adminInfo))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
        verify(parentCategoryRepository, never()).findAllByDeletedAtIsNullOrderBySortOrderAsc();
    }

    @Test
    @DisplayName("카테고리 관리 실패 - DB 기준 관리자 권한 없음")
    void 카테고리_관리_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(csAdmin()));

        // when & then
        assertThatThrownBy(() -> adminCategoryService.getParentCategories(adminInfo))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(parentCategoryRepository, never()).findAllByDeletedAtIsNullOrderBySortOrderAsc();
    }

    @Test
    @DisplayName("카테고리 관리 실패 - 관리자 BLOCKED")
    void 카테고리_관리_실패_관리자BLOCKED() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin blockedAdmin = Admin.builder()
                .email("admin@example.com")
                .password("encodedPassword")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.BLOCKED)
                .build();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(blockedAdmin));

        // when & then
        assertThatThrownBy(() -> adminCategoryService.getParentCategories(adminInfo))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(parentCategoryRepository, never()).findAllByDeletedAtIsNullOrderBySortOrderAsc();
    }

    private AdminCategoryRequestDto request(String categoryName, String slug, Integer sortOrder, Integer isActive) {
        return AdminCategoryRequestDto.builder()
                .categoryName(categoryName)
                .slug(slug)
                .sortOrder(sortOrder)
                .isActive(isActive)
                .build();
    }

    private AdminJwtUserInfoDto superAdminInfo() {
        return AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.SUPER_ADMIN)
                .build();
    }

    private AdminJwtUserInfoDto csAdminInfo() {
        return AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
    }

    private Admin superAdmin() {
        return Admin.builder()
                .email("admin@example.com")
                .password("encodedPassword")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }

    private Admin csAdmin() {
        return Admin.builder()
                .email("admin@example.com")
                .password("encodedPassword")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.CS_ADMIN)
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }

    private ParentCategory parentCategory(Long id, String categoryName, String slug, Integer sortOrder, Integer isActive) {
        return ParentCategory.builder()
                .parentCategoryId(id)
                .categoryName(categoryName)
                .slug(slug)
                .sortOrder(sortOrder)
                .isActive(isActive)
                .build();
    }

    private ChildCategory childCategory(
            Long id,
            ParentCategory parentCategory,
            String categoryName,
            String slug,
            Integer sortOrder,
            Integer isActive
    ) {
        return ChildCategory.builder()
                .childCategoryId(id)
                .parentCategory(parentCategory)
                .categoryName(categoryName)
                .slug(slug)
                .sortOrder(sortOrder)
                .isActive(isActive)
                .build();
    }
}
