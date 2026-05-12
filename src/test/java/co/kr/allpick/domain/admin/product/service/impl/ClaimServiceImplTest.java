package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    ClaimRepository claimRepository;

    @Mock
    AdminRepository adminRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    SellerRepository sellerRepository;
    
    @InjectMocks
    ClaimServiceImpl claimService;

    // 테스트용 OrderItem 객체 생성 헬퍼 (기본 상품가격 29000원)
    private OrderItem buildMockOrderItem(Long memberId) {
        return buildMockOrderItem(memberId, BigDecimal.valueOf(29000));
    }

    // 테스트용 OrderItem 객체 생성 헬퍼 (상품가격 직접 지정)
    private OrderItem buildMockOrderItem(Long memberId, BigDecimal productPrice) {
        // [교정] UnfinishedStubbing 방지를 위해 doReturn 스타일 사용
        Member mockMember = Mockito.mock(Member.class);
        doReturn(memberId).when(mockMember).getId();

        Order mockOrder = Order.builder()
                .member(mockMember)
                .addressId(1L)
                .orderNumber("TEST-001")
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .shippingFee(0)
                .status(Order.OrderStatus.PAID)
                .orderedAt(LocalDateTime.now())
                .build();

        Product mockProduct = Mockito.mock(Product.class);

        // 엔티티 구조에 맞춰 productName 삭제 및 product 객체 주입
        return OrderItem.builder()
                .order(mockOrder)
                .product(mockProduct)
                .productPrice(productPrice)
                .quantity(1)
                .totalPrice(productPrice)
                .build();
    }

    // 테스트용 Claim 객체 생성 헬퍼
    private Claim buildMockClaim() {
        return Claim.builder()
                .memberId(1L)
                .orderItemId(10L)
                .claimType(Claim.ClaimType.RETURN)
                .reasonCode(Claim.ReasonCode.CHANGE_MIND)
                .detail("단순 변심입니다.")
                .pickupMethod(Claim.ClaimPickupMethod.COURIER)
                .rejectReason(null)
                .refundAmount(BigDecimal.valueOf(29000))
                .shippingFee(BigDecimal.valueOf(3000))
                .build();
    }

    private Admin buildAdmin(Admin.AdminRole role, Admin.AdminStatus status) {
        return Admin.builder()
                .email("admin@test.com")
                .password("encodedPassword")
                .adminName("관리자")
                .adminPhone("01012345678")
                .role(role)
                .status(status)
                .build();
    }

    private AdminJwtUserInfoDto superAdminInfo(Long adminId) {
        return AdminJwtUserInfoDto.builder()
                .adminId(adminId)
                .email("admin@test.com")
                .role(Admin.AdminRole.SUPER_ADMIN)
                .build();
    }

    @Test
    @DisplayName("클레임 등록 성공 - 단순 변심 (왕복 배송비 6000원 차감)")
    void 클레임_등록_성공() {
        // given – refundAmount/shippingFee는 서비스에서 자동 계산되므로 DTO에 전달해도 무시됨
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, "단순 변심입니다.",
                Claim.ClaimPickupMethod.COURIER, null,
                null, null);

        OrderItem mockItem = buildMockOrderItem(1L); // productPrice=29000, memberId=1L
        Claim mockClaim = buildMockClaim();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        // when
        ClaimResponseDto result = claimService.createClaim(1L, request);

        // then – 반환 DTO 기본 검증
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getClaimType()).isEqualTo(Claim.ClaimType.RETURN);
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);

        // 배송비 자동 계산 검증: CHANGE_MIND(단순변심) → 왕복 배송비 6000원, 환불 = 29000 - 6000 = 23000
        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(captor.capture());
        Claim saved = captor.getValue();
        assertThat(saved.getShippingFee()).isEqualByComparingTo(BigDecimal.valueOf(6000));
        assertThat(saved.getRefundAmount()).isEqualByComparingTo(BigDecimal.valueOf(23000));
    }

    @Test
    @DisplayName("클레임 등록 성공 - 판매자 귀책 사유 (배송비 없음, 전액 환불)")
    void 클레임_등록_성공_판매자귀책_배송비없음() {
        // given – DEFECT(상품불량)는 판매자 귀책 → 배송비 0원, 전액 환불
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.DEFECT, "상품이 파손된 채로 도착했습니다.",
                Claim.ClaimPickupMethod.COURIER, null,
                null, null);

        OrderItem mockItem = buildMockOrderItem(1L); // productPrice=29000
        Claim mockClaim = buildMockClaim();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        // when
        claimService.createClaim(1L, request);

        // then – shippingFee=0, refundAmount=29000(전액)
        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(captor.capture());
        Claim saved = captor.getValue();
        assertThat(saved.getShippingFee()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getRefundAmount()).isEqualByComparingTo(BigDecimal.valueOf(29000));
    }

    @Test
    @DisplayName("클레임 등록 성공 - 상품금액 < 왕복배송비 → 환불금액 음수 (추가 결제 필요)")
    void 클레임_등록_성공_추가결제필요() {
        // given – 상품가격 5000원, 단순 변심 → 배송비 6000원 → refundAmount = -1000
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, "단순 변심입니다.",
                Claim.ClaimPickupMethod.COURIER, null,
                null, null);

        OrderItem mockItem = buildMockOrderItem(1L, BigDecimal.valueOf(5000));
        Claim mockClaim = buildMockClaim();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        // when
        claimService.createClaim(1L, request);

        // then – refundAmount가 음수(고객이 1000원 추가 결제해야 함)
        ArgumentCaptor<Claim> captor = ArgumentCaptor.forClass(Claim.class);
        verify(claimRepository).save(captor.capture());
        Claim saved = captor.getValue();
        assertThat(saved.getShippingFee()).isEqualByComparingTo(BigDecimal.valueOf(6000));
        assertThat(saved.getRefundAmount()).isEqualByComparingTo(BigDecimal.valueOf(-1000));
        assertThat(saved.getRefundAmount().signum()).isNegative();
    }

    @Test
    @DisplayName("클레임 등록 실패 - 다른 회원의 주문 상품")
    void 클레임_등록_실패_다른회원_주문() {
        // given – OrderItem 소유자가 memberId=2L, 요청자는 memberId=1L
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        OrderItem mockItem = buildMockOrderItem(2L); // 소유자 memberId=2L

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_UNAUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("클레임 등록 실패 - 이미 처리 중인 클레임 존재")
    void 클레임_등록_실패_중복클레임() {
        // given – 동일 주문 상품에 대해 이미 활성 클레임이 존재
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        OrderItem mockItem = buildMockOrderItem(1L);

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));
        when(claimRepository.existsByOrderItemIdAndStatusNotIn(anyLong(), anyList())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("클레임 등록 실패 - 존재하지 않는 회원")
    void 클레임_등록_실패_회원없음() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        when(memberRepository.existsById(999L)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 등록 실패 - 존재하지 않는 주문 상품")
    void 클레임_등록_실패_주문상품없음() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                999L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 등록 실패 - 반품 요청에 교환 전용 사유 사용")
    void 클레임_등록_실패_반품에_교환사유() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.SIZE_CHANGE, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        when(memberRepository.existsById(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_REASON_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("클레임 등록 실패 - 교환 요청에 반품 전용 사유 사용")
    void 클레임_등록_실패_교환에_반품사유() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.EXCHANGE,
                Claim.ReasonCode.CHANGE_MIND, null,
                Claim.ClaimPickupMethod.COURIER, null, null, null);

        when(memberRepository.existsById(1L)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> claimService.createClaim(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_REASON_MISMATCH.getMessage());
    }

    @Test
    @DisplayName("클레임 상세 조회 성공")
    void 클레임_상세_조회_성공() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.getClaimById(claimId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getReasonCode()).isEqualTo(Claim.ReasonCode.CHANGE_MIND);
    }

    @Test
    @DisplayName("클레임 상세 조회 실패 - 클레임 없음")
    void 클레임_상세_조회_실패_클레임없음() {
        // given
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> claimService.getClaimById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("내 클레임 목록 조회 성공")
    void 내_클레임_목록_조회_성공() {
        // given
        Long memberId = 1L;
        Claim mockClaim1 = buildMockClaim();
        Claim mockClaim2 = Claim.builder()
                .memberId(memberId)
                .orderItemId(20L)
                .claimType(Claim.ClaimType.EXCHANGE)
                .build();

        when(claimRepository.findByMemberIdAndDeletedAtIsNull(memberId))
                .thenReturn(List.of(mockClaim1, mockClaim2));

        // when
        List<ClaimResponseDto> result = claimService.getMyClaims(memberId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getClaimType()).isEqualTo(Claim.ClaimType.RETURN);
        assertThat(result.get(1).getClaimType()).isEqualTo(Claim.ClaimType.EXCHANGE);
    }

    @Test
    @DisplayName("전체 클레임 목록 조회 성공")
    void 전체_클레임_목록_조회_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo(1L);
        Claim mockClaim = buildMockClaim();
        given(adminRepository.findById(adminInfo.getAdminId()))
                .willReturn(Optional.of(buildAdmin(Admin.AdminRole.SUPER_ADMIN, Admin.AdminStatus.ACTIVE)));
        given(claimRepository.findAllByDeletedAtIsNull()).willReturn(List.of(mockClaim));
        given(orderItemRepository.findAllWithOrderMemberAndProductByOrderItemIdIn(List.of(10L)))
                .willReturn(List.of());

        // when
        List<ClaimResponseDto> result = claimService.getAllClaims(adminInfo);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("클레임 상태 변경 성공")
    void 클레임_상태_변경_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo(1L);
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        given(adminRepository.findById(adminInfo.getAdminId()))
                .willReturn(Optional.of(buildAdmin(Admin.AdminRole.SUPER_ADMIN, Admin.AdminStatus.ACTIVE)));
        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.updateStatus(adminInfo, claimId, request);

        // then
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("클레임 상태 변경 실패 - 이미 처리 완료")
    void 클레임_상태_변경_실패_이미완료() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo(1L);
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.updateStatus(Claim.ClaimStatus.COMPLETED);
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        given(adminRepository.findById(adminInfo.getAdminId()))
                .willReturn(Optional.of(buildAdmin(Admin.AdminRole.SUPER_ADMIN, Admin.AdminStatus.ACTIVE)));
        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(adminInfo, claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_INVALID_STATUS.getMessage());
    }

    @Test
    @DisplayName("클레임 거부 성공")
    void 클레임_거부_성공() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo(1L);
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("사유");

        given(adminRepository.findById(adminInfo.getAdminId()))
                .willReturn(Optional.of(buildAdmin(Admin.AdminRole.SUPER_ADMIN, Admin.AdminStatus.ACTIVE)));
        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.rejectClaim(adminInfo, claimId, request);

        // then
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.REJECTED);
    }

    @Test
    @DisplayName("클레임 거부 실패 - 이미 취소된 클레임")
    void 클레임_거부_실패_이미취소() {
        // given
        AdminJwtUserInfoDto adminInfo = superAdminInfo(1L);
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.cancel();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("거부 사유");

        given(adminRepository.findById(adminInfo.getAdminId()))
                .willReturn(Optional.of(buildAdmin(Admin.AdminRole.SUPER_ADMIN, Admin.AdminStatus.ACTIVE)));
        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.rejectClaim(adminInfo, claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_CANCELLED.getMessage());
    }
    
    @Test
    @DisplayName("클레임 승인 실패 - 판매자가 아닌 회원")
    void approveClaim_notSeller_throwException() {
        // given
        Long claimId = 1L;
        Long memberId = 1L;

        Claim mockClaim = buildMockClaim();

        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));
        given(sellerRepository.existsByMemberIdAndDeletedAtIsNull(memberId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> claimService.approveClaim(claimId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("클레임 승인 성공 - 판매자 본인")
    void approveClaim_validSeller_success() {
        // given
        Long claimId = 1L;
        Long memberId = 1L;
        Long orderItemId = 10L;

        Claim mockClaim = buildMockClaim();

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        Seller mockSeller = Seller.builder().member(mockMember).build();
        OrderItem mockOrderItem = mock(OrderItem.class);
        Product mockProduct = mock(Product.class);
        given(mockProduct.getSeller()).willReturn(mockSeller);
        given(mockOrderItem.getProduct()).willReturn(mockProduct);

        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));
        given(sellerRepository.existsByMemberIdAndDeletedAtIsNull(memberId)).willReturn(true);
        given(orderItemRepository.findByIdWithSellerMember(orderItemId)).willReturn(Optional.of(mockOrderItem));

        // when & then
        claimService.approveClaim(claimId, memberId);
    }
}
