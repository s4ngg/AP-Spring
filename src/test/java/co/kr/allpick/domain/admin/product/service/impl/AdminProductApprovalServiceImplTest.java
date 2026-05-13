package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.domain.admin.product.entity.ProductApproval;
import co.kr.allpick.domain.admin.product.repository.ProductApprovalRepository;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
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
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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

    // ─────────────────────────────────────────
    // approveProduct
    // ─────────────────────────────────────────

    @Test
    @DisplayName("상품 승인 성공")
    void 상품_승인_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        Admin admin = mockSuperAdmin();
        Product product = product(Product.ApprovalStatus.PENDING, SellerStatus.APPROVED);

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
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getAdminId()).isEqualTo(1L);

        ArgumentCaptor<ProductApproval> captor = ArgumentCaptor.forClass(ProductApproval.class);
        then(productApprovalRepository).should().save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ProductApproval.ApprovalStatus.APPROVED);
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
        Admin admin = mockSuperAdmin();
        Product product = product(Product.ApprovalStatus.PENDING, SellerStatus.APPROVED);
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(admin));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L)).willReturn(Optional.of(product));
        given(productApprovalRepository.save(any(ProductApproval.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ProductApprovalResponseDto result = adminProductApprovalService.rejectProduct(adminInfo, 1L, request);

        // then
        assertThat(product.getApprovalStatus()).isEqualTo(Product.ApprovalStatus.SUSPENDED);
        assertThat(result.getStatus()).isEqualTo(ProductApproval.ApprovalStatus.REJECTED);
        assertThat(result.getRequestType()).isEqualTo(ProductApproval.RequestType.REGISTER);
        assertThat(result.getRejectReason()).isEqualTo("상품 설명 보완 필요");
        assertThat(result.getProductId()).isEqualTo(1L);
        assertThat(result.getAdminId()).isEqualTo(1L);

        ArgumentCaptor<ProductApproval> captor = ArgumentCaptor.forClass(ProductApproval.class);
        then(productApprovalRepository).should().save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ProductApproval.ApprovalStatus.REJECTED);
        assertThat(captor.getValue().getRejectReason()).isEqualTo("상품 설명 보완 필요");
        assertThat(captor.getValue().getProcessedAt()).isNotNull();
    }

    @Test
    @DisplayName("상품 승인 실패 - adminInfo null")
    void 상품_승인_실패_adminInfo_null() {
        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(null, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(adminRepository).should(never()).findById(any());
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - SUPER_ADMIN 권한 없음")
    void 상품_승인_실패_권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.CS_ADMIN)
                .build();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(csAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - 관리자 없음")
    void 상품_승인_실패_관리자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_NOT_FOUND);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - BLOCKED 관리자")
    void 상품_승인_실패_BLOCKED관리자() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(blockedAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - DB 기준 관리자 권한 없음")
    void 상품_승인_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(csAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
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
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        then(productApprovalRepository).should(never()).save(any());
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
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.APPROVAL_NOT_PENDING);
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - 이미 거절된 상품")
    void 상품_승인_실패_이미거절됨() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.SUSPENDED)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.APPROVAL_NOT_PENDING);
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 실패 - 판매자 미승인")
    void 상품_승인_실패_판매자미승인() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.PENDING, SellerStatus.PENDING)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.approveProduct(adminInfo, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SELLER_NOT_APPROVED);
        then(productApprovalRepository).should(never()).save(any());
    }

    // ─────────────────────────────────────────
    // rejectProduct
    // ─────────────────────────────────────────

    @Test
    @DisplayName("상품 승인 거절 실패 - adminInfo null")
    void 상품_승인_거절_실패_adminInfo_null() {
        // given
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(null, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(adminRepository).should(never()).findById(any());
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
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
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(csAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - 관리자 없음")
    void 상품_승인_거절_실패_관리자없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_NOT_FOUND);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - BLOCKED 관리자")
    void 상품_승인_거절_실패_BLOCKED관리자() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(blockedAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - DB 기준 관리자 권한 없음")
    void 상품_승인_거절_실패_DB관리자권한없음() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");
        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(csAdmin()));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ADMIN_FORBIDDEN);
        then(productRepository).should(never()).findByProductIdAndDeletedAtIsNull(any());
        then(productApprovalRepository).should(never()).save(any());
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
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        then(productApprovalRepository).should(never()).save(any());
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
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.APPROVAL_NOT_PENDING);
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - 이미 거절된 상품")
    void 상품_승인_거절_실패_이미거절됨() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.SUSPENDED)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.APPROVAL_NOT_PENDING);
        then(productApprovalRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("상품 승인 거절 실패 - 판매자 미승인")
    void 상품_승인_거절_실패_판매자미승인() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo();
        ProductRejectRequestDto request = new ProductRejectRequestDto("상품 설명 보완 필요");

        given(adminRepository.findById(adminInfo.getAdminId())).willReturn(Optional.of(superAdmin()));
        given(productRepository.findByProductIdAndDeletedAtIsNull(1L))
                .willReturn(Optional.of(product(Product.ApprovalStatus.PENDING, SellerStatus.PENDING)));

        // when & then
        assertThatThrownBy(() -> adminProductApprovalService.rejectProduct(adminInfo, 1L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SELLER_NOT_APPROVED);
        then(productApprovalRepository).should(never()).save(any());
    }

    // ─────────────────────────────────────────
    // 픽스처
    // ─────────────────────────────────────────

    private AdminJwtUserInfoDto superAdminInfo() {
        return AdminJwtUserInfoDto.builder()
                .adminId(1L)
                .role(Admin.AdminRole.SUPER_ADMIN)
                .build();
    }

    private Admin mockSuperAdmin() {
        Admin admin = mock(Admin.class);
        given(admin.getAdminId()).willReturn(1L);
        given(admin.getRole()).willReturn(Admin.AdminRole.SUPER_ADMIN);
        given(admin.getStatus()).willReturn(Admin.AdminStatus.ACTIVE);
        return admin;
    }

    private Admin superAdmin() {
        return Admin.builder()
                .email("dummy@test.local")
                .password("DUMMY_PASSWORD")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }

    private Admin csAdmin() {
        return Admin.builder()
                .email("dummy@test.local")
                .password("DUMMY_PASSWORD")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.CS_ADMIN)
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }

    private Admin blockedAdmin() {
        return Admin.builder()
                .email("dummy@test.local")
                .password("DUMMY_PASSWORD")
                .adminName("관리자")
                .adminPhone("010-0000-0000")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .status(Admin.AdminStatus.BLOCKED)
                .build();
    }

    private Product product(Product.ApprovalStatus approvalStatus, SellerStatus sellerStatus) {
        Seller seller = Seller.builder()
                .businessName("테스트 판매자")
                .businessNumber("123-45-67890")
                .representativeName("홍길동")
                .status(sellerStatus)
                .build();
        return Product.builder()
                .productId(1L)
                .seller(seller)
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

    private Product product(Product.ApprovalStatus approvalStatus) {
        return product(approvalStatus, SellerStatus.APPROVED);
    }
}
