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
import org.junit.jupiter.api.Nested;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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

    // ─── 픽스처 ───

    private Admin buildAdmin(Admin.AdminStatus status) {
        return Admin.builder()
                .email("super@allpick.com")
                .password("encodedPassword")
                .adminName("테스트관리자")
                .adminPhone("01099998888")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(status)
                .build();
    }

    private AdminJwtUserInfoDto superAdminInfo(Long adminId) {
        return AdminJwtUserInfoDto.builder()
                .adminId(adminId)
                .email("super@allpick.com")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .build();
    }

    private AdminJwtUserInfoDto csAdminInfo(Long adminId) {
        return AdminJwtUserInfoDto.builder()
                .adminId(adminId)
                .email("cs@allpick.com")
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
    }

    // ─────────────────────────────────────────
    // adminLogin
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("관리자 로그인")
    class AdminLogin {

        @Test
        @DisplayName("로그인 성공 - 토큰 반환 및 마지막 로그인 시각 갱신")
        void 로그인_성공() {
            Admin admin = spy(buildAdmin(Admin.AdminStatus.ACTIVE));
            AdminLoginRequestDto request = new AdminLoginRequestDto("super@allpick.com", "rawPassword");

            given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));
            given(passwordEncoder.matches(request.getPassword(), admin.getPassword())).willReturn(true);
            given(jwtProvider.createToken(any(AdminJwtUserInfoDto.class))).willReturn("mock-token");

            AdminLoginResponseDto response = adminService.adminLogin(request);

            assertThat(response).isNotNull();
            verify(admin).updateLastLoginAt(any());
        }

        @Test
        @DisplayName("로그인 실패 - 관리자 없음")
        void 로그인_실패_관리자없음() {
            AdminLoginRequestDto request = new AdminLoginRequestDto("none@allpick.com", "pw");
            given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.adminLogin(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
        }

        @Test
        @DisplayName("로그인 실패 - BLOCKED 계정")
        void 로그인_실패_BLOCKED() {
            Admin admin = buildAdmin(Admin.AdminStatus.BLOCKED);
            AdminLoginRequestDto request = new AdminLoginRequestDto("super@allpick.com", "pw");
            given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));

            assertThatThrownBy(() -> adminService.adminLogin(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_BLOCKED);
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void 로그인_실패_비밀번호불일치() {
            Admin admin = buildAdmin(Admin.AdminStatus.ACTIVE);
            AdminLoginRequestDto request = new AdminLoginRequestDto("super@allpick.com", "wrong");
            given(adminRepository.findByEmail(request.getEmail())).willReturn(Optional.of(admin));
            given(passwordEncoder.matches(request.getPassword(), admin.getPassword())).willReturn(false);

            assertThatThrownBy(() -> adminService.adminLogin(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);
        }
    }

    // ─────────────────────────────────────────
    // createAdmin
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("관리자 등록")
    class CreateAdmin {

        private AdminCreateRequestDto buildRequest() {
            return AdminCreateRequestDto.builder()
                    .email("new@allpick.com")
                    .password("NewAdmin1!")
                    .adminName("신규관리자")
                    .adminPhone("01099998888")
                    .role(Admin.AdminRole.CS_ADMIN)
                    .build();
        }

        @Test
        @DisplayName("등록 성공 - 비밀번호 인코딩 및 저장 호출 확인")
        void 등록_성공() {
            AdminCreateRequestDto request = buildRequest();
            given(adminRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(passwordEncoder.encode(request.getPassword())).willReturn("encodedNew");

            adminService.createAdmin(superAdminInfo(1L), request);

            verify(passwordEncoder).encode(request.getPassword());
            then(adminRepository).should().save(any(Admin.class));
        }

        @Test
        @DisplayName("등록 실패 - adminInfo null → ADMIN_FORBIDDEN")
        void 등록_실패_adminInfo_null() {
            assertThatThrownBy(() -> adminService.createAdmin(null, buildRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
            then(adminRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("등록 실패 - CS_ADMIN은 등록 불가")
        void 등록_실패_CS_ADMIN_권한없음() {
            assertThatThrownBy(() -> adminService.createAdmin(csAdminInfo(2L), buildRequest()))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
            then(adminRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("등록 실패 - 이메일 중복")
        void 등록_실패_이메일중복() {
            AdminCreateRequestDto request = buildRequest();
            given(adminRepository.existsByEmail(request.getEmail())).willReturn(true);

            assertThatThrownBy(() -> adminService.createAdmin(superAdminInfo(1L), request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_EMAIL_DUPLICATED);
            then(adminRepository).should(never()).save(any());
        }
    }

    // ─────────────────────────────────────────
    // updateStatus
    // ─────────────────────────────────────────

    @Nested
    @DisplayName("관리자 상태 변경")
    class UpdateStatus {

        @Test
        @DisplayName("상태 변경 성공 - ACTIVE → BLOCKED")
        void 상태변경_성공_ACTIVE_to_BLOCKED() {
            Admin target = spy(buildAdmin(Admin.AdminStatus.ACTIVE));
            given(adminRepository.findById(2L)).willReturn(Optional.of(target));

            adminService.updateStatus(superAdminInfo(1L), 2L, Admin.AdminStatus.BLOCKED);

            verify(target).updateStatus(Admin.AdminStatus.BLOCKED);
        }

        @Test
        @DisplayName("상태 변경 성공 - BLOCKED → ACTIVE")
        void 상태변경_성공_BLOCKED_to_ACTIVE() {
            Admin target = spy(buildAdmin(Admin.AdminStatus.BLOCKED));
            given(adminRepository.findById(2L)).willReturn(Optional.of(target));

            adminService.updateStatus(superAdminInfo(1L), 2L, Admin.AdminStatus.ACTIVE);

            verify(target).updateStatus(Admin.AdminStatus.ACTIVE);
        }

        @Test
        @DisplayName("상태 변경 실패 - 본인 계정 변경 시도")
        void 상태변경_실패_본인차단() {
            assertThatThrownBy(() -> adminService.updateStatus(superAdminInfo(1L), 1L, Admin.AdminStatus.BLOCKED))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_CANNOT_BLOCK_SELF);
            then(adminRepository).should(never()).findById(any());
        }

        @Test
        @DisplayName("상태 변경 실패 - 대상 관리자 없음")
        void 상태변경_실패_대상없음() {
            given(adminRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> adminService.updateStatus(superAdminInfo(1L), 99L, Admin.AdminStatus.BLOCKED))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
        }

        @Test
        @DisplayName("상태 변경 실패 - CS_ADMIN은 상태 변경 불가")
        void 상태변경_실패_CS_ADMIN_권한없음() {
            assertThatThrownBy(() -> adminService.updateStatus(csAdminInfo(2L), 3L, Admin.AdminStatus.BLOCKED))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
            then(adminRepository).should(never()).findById(any());
        }
    }
}
