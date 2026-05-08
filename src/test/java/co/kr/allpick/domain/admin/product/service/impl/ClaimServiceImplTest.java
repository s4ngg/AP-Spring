package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.entity.ProductOption;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    ClaimRepository claimRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    SellerRepository sellerRepository;
    
    @InjectMocks
    ClaimServiceImpl claimService;

    // ?åÏä§?∏Ïö© OrderItem Í∞ùÏ≤¥ ?ùÏÑ± ?¨Ìçº
    private OrderItem buildMockOrderItem(Long memberId) {
        // [ÍµêÏ†ï] UnfinishedStubbing Î∞©Ï?Î•??ÑÌï¥ doReturn ?§Ì????¨Ïö©
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
        ProductOption mockOption = Mockito.mock(ProductOption.class);

        // productPrice * quantity Î°?totalPriceÍ∞Ä Í≥ÑÏÇ∞?òÎ?Î°?totalPrice ?ÑÎìú ?ÜÏùå
        return OrderItem.builder()
                .order(mockOrder)
                .product(mockProduct)
                .productOption(mockOption)
                .productName("?åÏä§???ÅÌíà")
                .productPrice(BigDecimal.valueOf(29000))
                .quantity(1)
                .build();
    }

    // ?åÏä§?∏Ïö© Claim Í∞ùÏ≤¥ ?ùÏÑ± ?¨Ìçº
    private Claim buildMockClaim() {
        return Claim.builder()
                .memberId(1L)
                .orderItemId(10L)
                .claimType(Claim.ClaimType.RETURN)
                .reasonCode(Claim.ReasonCode.CHANGE_MIND)
                .detail("?®Ïàú Î≥Ä?¨ÏûÖ?àÎã§.")
                .pickupMethod(Claim.ClaimPickupMethod.COURIER)
                .rejectReason(null)
                .refundAmount(BigDecimal.valueOf(29000))
                .shippingFee(BigDecimal.valueOf(3000))
                .build();
    }

    @Test
    @DisplayName("?¥Î†à???±Î°ù ?±Í≥µ")
    void ?¥Î†à???±Î°ù_?±Í≥µ() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, "?®Ïàú Î≥Ä?¨ÏûÖ?àÎã§.",
                Claim.ClaimPickupMethod.COURIER, null,
                BigDecimal.valueOf(29000), BigDecimal.valueOf(3000));

        // [?µÏã¨ ÍµêÏ†ï] buildMockOrderItem??when Î∞ñÏúºÎ°?Î∫çÎãà??
        OrderItem mockItem = buildMockOrderItem(1L);
        Claim mockClaim = buildMockClaim();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findById(10L)).thenReturn(Optional.of(mockItem));
        when(claimRepository.save(any(Claim.class))).thenReturn(mockClaim);

        // when
        ClaimResponseDto result = claimService.createClaim(1L, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getClaimType()).isEqualTo(Claim.ClaimType.RETURN);
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
    }

    @Test
    @DisplayName("?¥Î†à???±Î°ù ?§Ìå® - Ï°¥Ïû¨?òÏ? ?äÎäî ?åÏõê")
    void ?¥Î†à???±Î°ù_?§Ìå®_?åÏõê?ÜÏùå() {
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
    @DisplayName("?¥Î†à???±Î°ù ?§Ìå® - Ï°¥Ïû¨?òÏ? ?äÎäî Ï£ºÎ¨∏ ?ÅÌíà")
    void ?¥Î†à???±Î°ù_?§Ìå®_Ï£ºÎ¨∏?ÅÌíà?ÜÏùå() {
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
    @DisplayName("?¥Î†à???±Î°ù ?§Ìå® - Î∞òÌíà ?îÏ≤≠??ÍµêÌôò ?ÑÏö© ?¨Ïú† ?¨Ïö©")
    void ?¥Î†à???±Î°ù_?§Ìå®_Î∞òÌíà??ÍµêÌôò?¨Ïú†() {
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
    @DisplayName("?¥Î†à???±Î°ù ?§Ìå® - ÍµêÌôò ?îÏ≤≠??Î∞òÌíà ?ÑÏö© ?¨Ïú† ?¨Ïö©")
    void ?¥Î†à???±Î°ù_?§Ìå®_ÍµêÌôò??Î∞òÌíà?¨Ïú†() {
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
    @DisplayName("?¥Î†à???ÅÏÑ∏ Ï°∞Ìöå ?±Í≥µ")
    void ?¥Î†à???ÅÏÑ∏_Ï°∞Ìöå_?±Í≥µ() {
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
    @DisplayName("?¥Î†à???ÅÏÑ∏ Ï°∞Ìöå ?§Ìå® - ?¥Î†à???ÜÏùå")
    void ?¥Î†à???ÅÏÑ∏_Ï°∞Ìöå_?§Ìå®_?¥Î†à?ÑÏóÜ??) {
        // given
        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> claimService.getClaimById(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("???¥Î†à??Î™©Î°ù Ï°∞Ìöå ?±Í≥µ")
    void ???¥Î†à??Î™©Î°ù_Ï°∞Ìöå_?±Í≥µ() {
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
    @DisplayName("?ÑÏ≤¥ ?¥Î†à??Î™©Î°ù Ï°∞Ìöå ?±Í≥µ")
    void ?ÑÏ≤¥_?¥Î†à??Î™©Î°ù_Ï°∞Ìöå_?±Í≥µ() {
        // given
        Claim mockClaim = buildMockClaim();
        when(claimRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(mockClaim));

        // when
        List<ClaimResponseDto> result = claimService.getAllClaims();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("?¥Î†à???ÅÌÉú Î≥ÄÍ≤??±Í≥µ")
    void ?¥Î†à???ÅÌÉú_Î≥ÄÍ≤??±Í≥µ() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.updateStatus(claimId, request);

        // then
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("?¥Î†à???ÅÌÉú Î≥ÄÍ≤??§Ìå® - ?¥Î? Ï≤òÎ¶¨ ?ÑÎ£å")
    void ?¥Î†à???ÅÌÉú_Î≥ÄÍ≤??§Ìå®_?¥Î??ÑÎ£å() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.updateStatus(Claim.ClaimStatus.COMPLETED);
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_INVALID_STATUS.getMessage());
    }

    @Test
    @DisplayName("?¥Î†à??Í±∞Î? ?±Í≥µ")
    void ?¥Î†à??Í±∞Î?_?±Í≥µ() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("?¨Ïú†");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.rejectClaim(claimId, request);

        // then
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.REJECTED);
    }

    @Test
    @DisplayName("?¥Î†à??Í±∞Î? ?§Ìå® - ?¥Î? Ï∑®ÏÜå???¥Î†à??)
    void ?¥Î†à??Í±∞Î?_?§Ìå®_?¥Î?Ï∑®ÏÜå() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.cancel();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("Í±∞Î? ?¨Ïú†");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.rejectClaim(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_CANCELLED.getMessage());
    }
    
    @Test
    @DisplayName("?¥Î†à???πÏù∏ ?§Ìå® - ?êÎß§?êÍ? ?ÑÎãå ?åÏõê")
    void approveClaim_notSeller_throwException() {
        // given
        Long claimId = 1L;
        Long memberId = 1L;

        Claim mockClaim = buildMockClaim();

        given(claimRepository.findById(claimId)).willReturn(Optional.of(mockClaim));
        given(sellerRepository.existsByMember_IdAndDeletedAtIsNull(memberId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> claimService.approveClaim(claimId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.NOT_SELLER.getMessage());
    }

    @Test
    @DisplayName("?¥Î†à???πÏù∏ ?±Í≥µ - ?êÎß§??Î≥∏Ïù∏")
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
        given(sellerRepository.existsByMember_IdAndDeletedAtIsNull(memberId)).willReturn(true);
        given(orderItemRepository.findByIdWithSellerMember(orderItemId)).willReturn(Optional.of(mockOrderItem));

        // when & then
        claimService.approveClaim(claimId, memberId);
    }
}