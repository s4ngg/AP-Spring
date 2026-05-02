package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.domain.admin.product.entity.ProductApproval;
import co.kr.allpick.domain.admin.product.repository.ProductApprovalRepository;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminProductApprovalServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    AdminRepository adminRepository;

    @Mock
    ProductApprovalRepository productApprovalRepository;

    @InjectMocks
    AdminProductApprovalServiceImpl adminProductApprovalService;

    @Test
    @DisplayName("상품 승인 성공")
    void 상품_승인_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = superAdmin();
        Product product = product(Product.ApprovalStatus.PENDING);

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(productApprovalRepository.save(any(ProductApproval.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ProductApprovalResponseDto result = adminProductApprovalService.approveProduct(adminInfo, 1L);

        // then
        assertThat(product.getApprovalStatus()).isEqualTo(Product.ApprovalStatus.APPROVED);
        assertThat(result.getStatus()).isEqualTo(ProductApproval.ApprovalStatus.APPROVED);
        assertThat(result.getRequestType()).isEqualTo(ProductApproval.RequestType.REGISTER);

        ArgumentCaptor<ProductApproval> captor = ArgumentCaptor.forClass(ProductApproval.class);
        verify(productApprovalRepository).save(captor.capture());
        assertThat(captor.getValue().getProduct()).isEqualTo(product);
        assertThat(captor.getValue().getAdmin()).isEqualTo(admin);
        assertThat(captor.getValue().getRejectReason()).isNull();
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("상품 승인 거절 성공")
    void 상품_승인_거절_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = superAdmin();
        Product product = product(Product.ApprovalStatus.PENDING);
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(productApprovalRepository.save(any(ProductApproval.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ProductApprovalResponseDto result = adminProductApprovalService.rejectProduct(adminInfo, 1L, request);

        // then
        assertThat(product.getApprovalStatus()).isEqualTo(Product.ApprovalStatus.REJECTED);
        assertThat(result.getStatus()).isEqualTo(ProductApproval.ApprovalStatus.REJECTED);
        assertThat(result.getRejectReason()).isEqualTo("상품 설명 보완 필요");

        ArgumentCaptor<ProductApproval> captor = ArgumentCaptor.forClass(ProductApproval.class);
        verify(productApprovalRepository).save(captor.capture());
        assertThat(captor.getValue().getRejectReason()).isEqualTo("상품 설명 보완 필요");
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("상품 승인 실패 - SUPER_ADMIN 권한 없음")
    void 상품_승인_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(productRepository, never()).findByProductIdAndDeletedAtIsNull(any());
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - DB 기준 관리자 권한 없음")
    void 상품_승인_실패_DB관리자권한없음() {
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

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(productRepository, never()).findByProductIdAndDeletedAtIsNull(any());
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - 상품 없음")
    void 상품_승인_실패_상품없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 999L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - PENDING 상태가 아님")
    void 상품_승인_실패_상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.APPROVED)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - SUPER_ADMIN 권한 없음")
    void 상품_승인_거절_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(productRepository, never()).findByProductIdAndDeletedAtIsNull(any());
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - DB 기준 관리자 권한 없음")
    void 상품_승인_거절_실패_DB관리자권한없음() {
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
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
        verify(productRepository, never()).findByProductIdAndDeletedAtIsNull(any());
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - 상품 없음")
    void 상품_승인_거절_실패_상품없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 999L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - PENDING 상태가 아님")
    void 상품_승인_거절_실패_상태전이불가() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.APPROVED)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
        verify(productApprovalRepository, never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - 거절 사유 없음")
    void 상품_승인_거절_실패_거절사유없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Product product = product(Product.ApprovalStatus.PENDING);
        ProductRejectRequestDto request = new ProductRejectRequestDto(" ");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);
        assertThat(product.getApprovalStatus()).isEqualTo(Product.ApprovalStatus.PENDING);
        verify(productApprovalRepository, never()).save(any());
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

    private Product product(Product.ApprovalStatus approvalStatus) {
        return Product.builder()
                .productId(1L)
                .productName("테스트 상품")
                .brand("브랜드")
                .thumbnailUrl("https://example.com/image.jpg")
                .description("상품 설명")
                .price(BigDecimal.valueOf(10000))
                .manufacturer("제조사")
                .origin("대한민국")
                .precaution("주의사항")
                .status(Product.Status.ON_SALE)
                .approvalStatus(approvalStatus)
                .build();
    }
}
