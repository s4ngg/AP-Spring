package co.kr.allpick.domain.admin.member.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceImplTest {

    @Mock
    AdminRepository adminRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AdminMemberServiceImpl adminMemberService;

    @Test
    @DisplayName("구매자 목록 조회 성공")
    void 구매자_목록_조회_성공() {
        // given
        Member m1 = member(1L, 1);
        Member m2 = member(2L, 0);
        given(adminRepository.findById(1L)).willReturn(Optional.of(superAdmin()));
        given(memberRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of(m1, m2));

        // when
        List<MemberListResponseDto> result = adminMemberService.getMembers(superAdminInfo());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("구매자 정지 성공 - 활성(1) → 정지(0)")
    void 구매자_정지_성공() {
        // given
        Member member = member(1L, 1);
        given(adminRepository.findById(1L)).willReturn(Optional.of(superAdmin()));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        adminMemberService.toggleMemberStatus(superAdminInfo(), 1L);

        // then
        assertThat(member.getStatus()).isEqualTo(0);
    }

    @Test
    @DisplayName("구매자 활성화 성공 - 정지(0) → 활성(1)")
    void 구매자_활성화_성공() {
        // given
        Member member = member(1L, 0);
        given(adminRepository.findById(1L)).willReturn(Optional.of(superAdmin()));
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        // when
        adminMemberService.toggleMemberStatus(superAdminInfo(), 1L);

        // then
        assertThat(member.getStatus()).isEqualTo(1);
    }

    @Test
    @DisplayName("상태변경 실패 - 회원 없음")
    void 상태변경_실패_회원없음() {
        // given
        given(adminRepository.findById(1L)).willReturn(Optional.of(superAdmin()));
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminMemberService.toggleMemberStatus(superAdminInfo(), 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("구매자 목록 조회 실패 - 관리자 정보 없음")
    void 구매자_목록_조회_실패_관리자정보없음() {
        // when & then
        assertThatThrownBy(() -> adminMemberService.getMembers(null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    @Test
    @DisplayName("구매자 목록 조회 실패 - CS 관리자 권한")
    void 구매자_목록_조회_실패_CS관리자권한() {
        // when & then
        assertThatThrownBy(() -> adminMemberService.getMembers(csAdminInfo()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    @Test
    @DisplayName("구매자 목록 조회 실패 - 관리자 없음")
    void 구매자_목록_조회_실패_관리자없음() {
        // given
        given(adminRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminMemberService.getMembers(superAdminInfo()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
    }

    @Test
    @DisplayName("구매자 상태변경 실패 - 비활성 관리자")
    void 구매자_상태변경_실패_비활성관리자() {
        // given
        given(adminRepository.findById(1L)).willReturn(Optional.of(blockedSuperAdmin()));

        // when & then
        assertThatThrownBy(() -> adminMemberService.toggleMemberStatus(superAdminInfo(), 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    private Member member(Long id, int status) {
        return Member.builder()
                .id(id)
                .email("test" + id + "@example.com")
                .name("테스터" + id)
                .phone("010-0000-000" + id)
                .address("서울시")
                .status(status)
                .build();
    }

    private AdminJwtUserInfoDto superAdminInfo() {
        return new AdminJwtUserInfoDto(1L, "admin@example.com", Admin.AdminRole.SUPER_ADMIN);
    }

    private AdminJwtUserInfoDto csAdminInfo() {
        return new AdminJwtUserInfoDto(2L, "cs@example.com", Admin.AdminRole.CS_ADMIN);
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

    private Admin blockedSuperAdmin() {
        return Admin.builder()
                .email("blocked@example.com")
                .password("encodedPassword")
                .adminName("차단관리자")
                .adminPhone("010-0000-0001")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.BLOCKED)
                .build();
    }
}
