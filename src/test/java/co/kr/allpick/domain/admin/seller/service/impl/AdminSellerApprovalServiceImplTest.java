package co.kr.allpick.domain.admin.seller.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.domain.admin.seller.entity.SellerApproval;
import co.kr.allpick.domain.admin.seller.repository.SellerApprovalRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(admin));
        when(sellerRepository.findBySellerIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(seller));
        when(sellerApprovalRepository.save(any(SellerApproval.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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

        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(admin));
        when(sellerRepository.findBySellerIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(seller));
        when(sellerApprovalRepository.save(any(SellerApproval.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

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
                .hasMessage(ErrorCode.ADMIN_FORBIDDEN.getMessage());
        verify(sellerRepository, never()).findBySellerIdAndDeletedAtIsNull(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - 판매자 없음")
    void 판매자_승인_실패_판매자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(superAdmin()));
        when(sellerRepository.findBySellerIdAndDeletedAtIsNull(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.SELLER_NOT_FOUND.getMessage());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - DB 기준 관리자 권한 없음")
    void 판매자_승인_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = Admin.builder()
                .email("admin@example.com")
                .password("encodedPassword")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.CS_ADMIN)
                .status(Admin.AdminStatus.ACTIVE)
                .build();

        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ADMIN_FORBIDDEN.getMessage());
        verify(sellerRepository, never()).findBySellerIdAndDeletedAtIsNull(any());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 실패 - PENDING 상태가 아님")
    void 판매자_승인_실패_상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(superAdmin()));
        when(sellerRepository.findBySellerIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(seller(SellerStatus.APPROVED)));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.approveSeller(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_INPUT.getMessage());
        verify(sellerApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("판매자 승인 거절 실패 - 거절 사유 없음")
    void 판매자_승인_거절_실패_거절사유없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Seller seller = seller(SellerStatus.PENDING);
        SellerRejectRequestDto request = new SellerRejectRequestDto(" ");

        when(adminRepository.findById(adminInfo.getAdminId())).thenReturn(Optional.of(superAdmin()));
        when(sellerRepository.findBySellerIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(seller));

        // when & then
        assertThatThrownBy(() -> adminSellerApprovalService.rejectSeller(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_INPUT.getMessage());
        assertThat(seller.getStatus()).isEqualTo(SellerStatus.PENDING);
        verify(sellerApprovalRepository, never()).save(any());
    }

    private AdminJwtUserInfoDto superAdminInfo() {
        return AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.SUPER_ADMIN)
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
}
