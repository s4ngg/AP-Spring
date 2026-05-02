package co.kr.allpick.domain.admin.service.impl;

import co.kr.allpick.domain.admin.dto.AdminCreateRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginRequestDto;
import co.kr.allpick.domain.admin.dto.AdminLoginResponseDto;
import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.config.JwtProvider;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    // ─────────────────────────────────────────
    // 픽스처 메서드
    // ─────────────────────────────────────────

    private Admin createActiveAdmin(Long adminId) {
        Admin admin = mock(Admin.class);
        given(admin.getAdminId()).willReturn(adminId);
        given(admin.getStatus()).willReturn(Admin.AdminStatus.ACTIVE);
        given(admin.getRole()).willReturn(Admin.AdminRole.SUPER_ADMIN);
        given(admin.getPassword()).willReturn("encodedPassword");
        given(admin.getAdminName()).willReturn("테스트관리자");
        return admin;
    }

    private AdminJwtUserInfoDto createSuperAdminInfo(Long adminId) {
        return AdminJwtUserInfoDto.builder()
                .adminId(adminId)
                .email("super@allpick.com")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .build();
    }

    private AdminJwtUserInfoDto createCsAdminInfo(Long adminId) {
        return AdminJwtUserInfoDto.builder()
                .adminId(adminId)
                .email("cs@allpick.com")
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
    }

    // ─────────────────────────────────────────
    // adminLogin() 테스트
    // ─────────────────────────────────────────

    @Test
    @DisplayName("로그인 성공 - 토큰 반환 및 마지막 로그인 시각 업데이트")
    void adminLogin_success() {
        // given
        Admin admin = createActiveAdmin(1L);
        AdminLoginRequestDto request = new AdminLoginRequestDto("super@allpick.com", "rawPassword");

        given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(request.getPassword(), admin.getPassword())).willReturn(true);
        when(jwtProvider.createToken(any(AdminJwtUserInfoDto.class))).thenReturn("mock-token");

        // when
        AdminLoginResponseDto response = adminService.adminLogin(request);

        // then
        assertThat(response).isNotNull();
        verify(admin).updateLastLoginAt(any());
    }

    @Test
    @DisplayName("로그인 실패 - 관리자 없음")
    void adminLogin_fail_adminNotFound() {
        // given
        AdminLoginRequestDto request = new AdminLoginRequestDto("notexist@allpick.com", "rawPassword");

        given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.adminLogin(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패 - BLOCKED 계정")
    void adminLogin_fail_adminBlocked() {
        // given
        Admin admin = mock(Admin.class);
        given(admin.getStatus()).willReturn(Admin.AdminStatus.BLOCKED);
        AdminLoginRequestDto request = new AdminLoginRequestDto("blocked@allpick.com", "rawPassword");

        given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminService.adminLogin(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_BLOCKED);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void adminLogin_fail_invalidPassword() {
        // given
        Admin admin = createActiveAdmin(1L);
        AdminLoginRequestDto request = new AdminLoginRequestDto("super@allpick.com", "wrongPassword");

        given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));
        given(passwordEncoder.matches(request.getPassword(), admin.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> adminService.adminLogin(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
    }

    // ─────────────────────────────────────────
    // createAdmin() 테스트
    // ─────────────────────────────────────────

    @Test
    @DisplayName("관리자 등록 성공 - 비밀번호 인코딩 및 저장 호출 확인")
    void createAdmin_success() {
        // given
        AdminJwtUserInfoDto adminInfo = createSuperAdminInfo(1L);
        AdminCreateRequestDto request = AdminCreateRequestDto.builder()
                .email("new@allpick.com")
                .password("NewAdmin1!")
                .adminName("신규관리자")
                .adminPhone("01099998888")
                .role(Admin.AdminRole.CS_ADMIN)
                .build();

        given(adminRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedNewPassword");

        // when
        adminService.createAdmin(adminInfo, request);

        // then
        verify(passwordEncoder).encode(request.getPassword());
        then(adminRepository).should().save(any(Admin.class));
    }

    @Test
    @DisplayName("관리자 등록 실패 - adminInfo가 null이면 ADMIN_FORBIDDEN")
    void createAdmin_fail_adminInfoNull() {
        // given
        AdminCreateRequestDto request = AdminCreateRequestDto.builder()
                .email("new@allpick.com")
                .password("NewAdmin1!")
                .adminName("신규관리자")
                .adminPhone("01099998888")
                .role(Admin.AdminRole.CS_ADMIN)
                .build();

        // when & then
        assertThatThrownBy(() -> adminService.createAdmin(null, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);

        then(adminRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("관리자 등록 실패 - 이메일 중복")
    void createAdmin_fail_emailDuplicated() {
        // given
        AdminJwtUserInfoDto adminInfo = createSuperAdminInfo(1L);
        AdminCreateRequestDto request = AdminCreateRequestDto.builder()
                .email("duplicate@allpick.com")
                .password("NewAdmin1!")
                .adminName("중복관리자")
                .adminPhone("01011112222")
                .role(Admin.AdminRole.CS_ADMIN)
                .build();

        given(adminRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> adminService.createAdmin(adminInfo, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_EMAIL_DUPLICATED);

        then(adminRepository).should(never()).save(any());
    }

    // ─────────────────────────────────────────
    // updateStatus() 테스트
    // ─────────────────────────────────────────

    @Test
    @DisplayName("상태 변경 성공 - ACTIVE → BLOCKED")
    void updateStatus_success_activeToBlocked() {
        // given
        Long actorAdminId = 1L;
        Long targetAdminId = 2L;
        AdminJwtUserInfoDto adminInfo = createSuperAdminInfo(actorAdminId);
        Admin targetAdmin = mock(Admin.class);

        given(adminRepository.findById(targetAdminId)).willReturn(Optional.of(targetAdmin));

        // when
        adminService.updateStatus(adminInfo, targetAdminId, Admin.AdminStatus.BLOCKED);

        // then
        verify(targetAdmin).updateStatus(Admin.AdminStatus.BLOCKED);
    }

    @Test
    @DisplayName("상태 변경 실패 - 본인 계정 변경 시도")
    void updateStatus_fail_cannotBlockSelf() {
        // given
        Long adminId = 1L;
        AdminJwtUserInfoDto adminInfo = createSuperAdminInfo(adminId);

        // when & then
        assertThatThrownBy(() -> adminService.updateStatus(adminInfo, adminId, Admin.AdminStatus.BLOCKED))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_CANNOT_BLOCK_SELF);

        then(adminRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("상태 변경 실패 - 대상 관리자 없음")
    void updateStatus_fail_adminNotFound() {
        // given
        Long actorAdminId = 1L;
        Long targetAdminId = 99L;
        AdminJwtUserInfoDto adminInfo = createSuperAdminInfo(actorAdminId);

        given(adminRepository.findById(targetAdminId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.updateStatus(adminInfo, targetAdminId, Admin.AdminStatus.BLOCKED))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
    }

    @Test
    @DisplayName("상태 변경 실패 - CS_ADMIN은 상태 변경 불가")
    void updateStatus_fail_csAdminForbidden() {
        // given
        Long csAdminId = 2L;
        Long targetAdminId = 3L;
        AdminJwtUserInfoDto csAdminInfo = createCsAdminInfo(csAdminId);

        // when & then
        assertThatThrownBy(() -> adminService.updateStatus(csAdminInfo, targetAdminId, Admin.AdminStatus.BLOCKED))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);

        then(adminRepository).should(never()).findById(any());
    }
}
