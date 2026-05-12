package co.kr.allpick.domain.admin.seller.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerListResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.domain.admin.seller.entity.SellerApproval;
import co.kr.allpick.domain.admin.seller.repository.SellerApprovalRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
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
class AdminSellerApprovalServiceImplTest {

    @Mock
    SellerRepository sellerRepository;

    @Mock
    AdminRepository adminRepository;

    @Mock
    SellerApprovalRepository sellerApprovalRepository;

    @InjectMocks
    AdminSellerApprovalServiceImpl adminSellerApprovalService;

    @Test
    @DisplayName("판매자 승인 성공")
    void 판매자_승인_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = superAdmin();
        Seller seller = seller(SellerStatus.PENDING);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));
        given(sellerApprovalRepository.save(any(SellerApproval.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        SellerApprovalResponseDto result = adminSellerApprovalService.approveSeller(adminInfo, 1L);

        // then
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.APPROVED);
        assertThat(result.getStatus()).isEqualTo(SellerApproval.ApprovalStatus.APPROVED);
        assertThat(result.getRequestType()).isEqualTo(SellerApproval.RequestType.REGISTER);

        ArgumentCaptor<SellerApproval> captor = ArgumentCaptor.forClass(SellerApproval.class);
        verify(sellerApprovalRepository).save(captor.capture());
        assertThat(captor.getValue().getSeller()).isEqualTo(seller);
        assertThat(captor.getValue().getAdmin()).isEqualTo(admin);
        assertThat(captor.getValue().getRejectReason()).isNull();
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("판매자 승인 거절 성공")
    void 판매자_승인_거절_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = superAdmin();
        Seller seller = seller(SellerStatus.PENDING);
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));
        given(sellerApprovalRepository.save(any(SellerApproval.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        SellerApprovalResponseDto result = adminSellerApprovalService.rejectSeller(adminInfo, 1L, request);

        // then
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.REJECTED);
        assertThat(result.getStatus()).isEqualTo(SellerApproval.ApprovalStatus.REJECTED);
        assertThat(result.getRejectReason()).isEqualTo("사업자등록번호 확인 필요");

        ArgumentCaptor<SellerApproval> captor = ArgumentCaptor.forClass(SellerApproval.class);
        verify(sellerApprovalRepository).save(captor.capture());
        assertThat(captor.getValue().getRejectReason()).isEqualTo("사업자등록번호 확인 필요");
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("판매자 승인 실패 - SUPER_ADMIN 권한 없음")
    void 판매자_승인_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - 판매자 없음")
    void 판매자_승인_실패_판매자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - DB 기준 관리자 권한 없음")
    void 판매자_승인_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = csAdmin();

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - PENDING 상태가 아님")
    void 판매자_승인_실패_상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.APPROVED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - REJECTED 상태는 처리 불가")
    void 판매자_승인_실패_REJECTED상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.REJECTED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - SUSPENDED 상태는 처리 불가")
    void 판매자_승인_실패_SUSPENDED상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.SUSPENDED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - SUPER_ADMIN 권한 없음")
    void 판매자_승인_거절_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - DB 기준 관리자 권한 없음")
    void 판매자_승인_거절_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = csAdmin();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - 판매자 없음")
    void 판매자_승인_거절_실패_판매자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - PENDING 상태가 아님")
    void 판매자_승인_거절_실패_상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.APPROVED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - REJECTED 상태는 처리 불가")
    void 판매자_승인_거절_실패_REJECTED상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.REJECTED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - SUSPENDED 상태는 처리 불가")
    void 판매자_승인_거절_실패_SUSPENDED상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        SellerRejectRequestDto request = new SellerRejectRequestDto("사업자등록번호 확인 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller(SellerStatus.SUSPENDED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.APPROVAL_NOT_PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - 관리자 DB 조회 실패")
    void 판매자_승인_실패_관리자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_FOUND);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - SUPER_ADMIN이지만 BLOCKED 상태")
    void 판매자_승인_실패_관리자BLOCKED() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin blockedAdmin = Admin.builder()
                .email("admin@example.com")
                .password("ENCODED_DUMMY")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.BLOCKED)
                .build();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(blockedAdmin));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    // ── 판매자 목록 조회 / 상태 토글 테스트 ──────────────────────────────────

    @Test
    @DisplayName("판매자 목록 조회 성공 - APPROVED, SUSPENDED 포함")
    void 판매자_목록_조회_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller approvedSeller = sellerWithMember(SellerStatus.APPROVED);
        Seller suspendedSeller = sellerWithMember(SellerStatus.SUSPENDED);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findAllActiveMemberSellersByStatusIn(
                List.of(SellerStatus.APPROVED, SellerStatus.SUSPENDED)))
                .willReturn(List.of(approvedSeller, suspendedSeller));

        // when
        List<SellerListResponseDto> result = adminSellerApprovalService.getSellers(adminInfo);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStatus()).isEqualTo(SellerStatus.APPROVED);
        assertThat(result.get(1).getStatus()).isEqualTo(SellerStatus.SUSPENDED);
    }

    @Test
    @DisplayName("판매자 목록 조회 실패 - SUPER_ADMIN 권한 없음")
    void 판매자_목록_조회_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = csAdminInfo();

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.getSellers(adminInfo))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findAllActiveMemberSellersByStatusIn(any());
    }

    @Test
    @DisplayName("승인 대기 판매자 목록 조회 성공 - PENDING만 포함")
    void 승인대기_판매자_목록_조회_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller pendingSeller1 = sellerWithMember(SellerStatus.PENDING);
        Seller pendingSeller2 = sellerWithMember(SellerStatus.PENDING);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findAllActiveMemberSellersByStatus(SellerStatus.PENDING))
                .willReturn(List.of(pendingSeller1, pendingSeller2));

        // when
        List<SellerListResponseDto> result = adminSellerApprovalService.getPendingSellers(adminInfo);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(dto -> dto.getStatus() == SellerStatus.PENDING);
    }

    @Test
    @DisplayName("승인 대기 판매자 목록 조회 실패 - 인증 관리자 정보 없음")
    void 승인대기_판매자_목록_조회_실패_관리자정보없음() {
        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.getPendingSellers(null))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findAllActiveMemberSellersByStatus(any());
    }

    @Test
    @DisplayName("판매자 상태 토글 성공 - APPROVED → SUSPENDED")
    void 판매자_상태_토글_성공_승인에서정지() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller seller = seller(SellerStatus.APPROVED);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));

        // when
        adminSellerApprovalService.toggleSellerStatus(adminInfo, 1L);

        // then
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.SUSPENDED);
    }

    @Test
    @DisplayName("판매자 상태 토글 성공 - SUSPENDED → APPROVED")
    void 판매자_상태_토글_성공_정지에서승인() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller seller = seller(SellerStatus.SUSPENDED);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));

        // when
        adminSellerApprovalService.toggleSellerStatus(adminInfo, 1L);

        // then
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.APPROVED);
    }

    @Test
    @DisplayName("판매자 상태 토글 실패 - SUPER_ADMIN 권한 없음")
    void 판매자_상태_토글_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = csAdminInfo();

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.toggleSellerStatus(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(sellerRepository, never()).findActiveMemberSellerBySellerId(any());
    }

    @Test
    @DisplayName("판매자 상태 토글 실패 - PENDING 상태는 처리 불가")
    void 판매자_상태_토글_실패_PENDING상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller seller = seller(SellerStatus.PENDING);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.toggleSellerStatus(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_STATUS_NOT_TOGGLEABLE);
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.PENDING);
    }

    @Test
    @DisplayName("판매자 상태 토글 실패 - REJECTED 상태는 처리 불가")
    void 판매자_상태_토글_실패_REJECTED상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller seller = seller(SellerStatus.REJECTED);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(1L)).willReturn(Optional.of(seller));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.toggleSellerStatus(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_STATUS_NOT_TOGGLEABLE);
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.REJECTED);
    }

    @Test
    @DisplayName("판매자 상태 토글 실패 - 판매자 없음")
    void 판매자_상태_토글_실패_판매자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(sellerRepository.findActiveMemberSellerBySellerId(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.toggleSellerStatus(adminInfo, 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.SELLER_NOT_FOUND);
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

    private Seller seller(SellerStatus status) {
        return Seller.builder()
                .sellerId(1L)
                .businessName("올픽상점")
                .businessNumber("123-45-67890")
                .representativeName("대표자")
                .bankName("은행")
                .bankAccount("1234567890")
                .status(status)
                .build();
    }

    private Seller sellerWithMember(SellerStatus status) {
        Member member = Member.createLocal(
                "seller@test.com", "encodedPw", "판매자", "010-1234-5678", "서울시");
        return Seller.builder()
                .sellerId(1L)
                .member(member)
                .businessName("올픽상점")
                .businessNumber("123-45-67890")
                .representativeName("대표자")
                .bankName("은행")
                .bankAccount("1234567890")
                .status(status)
                .build();
    }
}
