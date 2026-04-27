package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceImplTest {

    @Mock
    ClaimRepository claimRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @InjectMocks
    ClaimServiceImpl claimService;

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

    @Test
    @DisplayName("클레임 등록 성공")
    void 클레임_등록_성공() {
        // given
        ClaimCreateRequestDto request = new ClaimCreateRequestDto(
                10L, null, Claim.ClaimType.RETURN,
                Claim.ReasonCode.CHANGE_MIND, "단순 변심입니다.",
                Claim.ClaimPickupMethod.COURIER, null,
                BigDecimal.valueOf(29000), BigDecimal.valueOf(3000));

        Claim mockClaim = buildMockClaim();

        when(memberRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.existsById(10L)).thenReturn(true);
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
        when(orderItemRepository.existsById(999L)).thenReturn(false);

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
        assertThat(result.getPickupMethod()).isEqualTo(Claim.ClaimPickupMethod.COURIER);
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
                .reasonCode(Claim.ReasonCode.SIZE_CHANGE)
                .pickupMethod(Claim.ClaimPickupMethod.VISIT)
                .rejectReason(null)
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
        Claim mockClaim = buildMockClaim();

        when(claimRepository.findAllByDeletedAtIsNull()).thenReturn(List.of(mockClaim));

        // when
        List<ClaimResponseDto> result = claimService.getAllClaims();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Claim.ClaimStatus.SUBMITTED);
    }

    @Test
    @DisplayName("클레임 상태 변경 성공")
    void 클레임_상태_변경_성공() {
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
    @DisplayName("클레임 상태 변경 실패 - 클레임 없음")
    void 클레임_상태_변경_실패_클레임없음() {
        // given
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 상태 변경 실패 - 이미 처리 완료")
    void 클레임_상태_변경_실패_이미완료() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.updateStatus(Claim.ClaimStatus.COMPLETED);
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_COMPLETED.getMessage());
    }

    @Test
    @DisplayName("클레임 상태 변경 실패 - 이미 거부된 클레임")
    void 클레임_상태_변경_실패_이미거부() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.reject("교환 기간 초과");
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_INVALID_STATUS.getMessage());
    }

    @Test
    @DisplayName("클레임 상태 변경 실패 - 이미 취소된 클레임")
    void 클레임_상태_변경_실패_이미취소() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.cancel();
        ClaimStatusUpdateRequestDto request = new ClaimStatusUpdateRequestDto(Claim.ClaimStatus.IN_PROGRESS);

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.updateStatus(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_CANCELLED.getMessage());
    }

    @Test
    @DisplayName("클레임 거부 성공")
    void 클레임_거부_성공() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("교환 기간이 초과되었습니다.");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when
        ClaimResponseDto result = claimService.rejectClaim(claimId, request);

        // then
        assertThat(result.getStatus()).isEqualTo(Claim.ClaimStatus.REJECTED);
        assertThat(result.getRejectReason()).isEqualTo("교환 기간이 초과되었습니다.");
    }

    @Test
    @DisplayName("클레임 거부 실패 - 클레임 없음")
    void 클레임_거부_실패_클레임없음() {
        // given
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("거부 사유");

        when(claimRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> claimService.rejectClaim(999L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("클레임 거부 실패 - 이미 처리 완료")
    void 클레임_거부_실패_이미완료() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.updateStatus(Claim.ClaimStatus.COMPLETED);
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("거부 사유");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.rejectClaim(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_COMPLETED.getMessage());
    }

    @Test
    @DisplayName("클레임 거부 실패 - 이미 취소된 클레임")
    void 클레임_거부_실패_이미취소() {
        // given
        Long claimId = 1L;
        Claim mockClaim = buildMockClaim();
        mockClaim.cancel();
        ClaimRejectRequestDto request = new ClaimRejectRequestDto("거부 사유");

        when(claimRepository.findById(claimId)).thenReturn(Optional.of(mockClaim));

        // when & then
        assertThatThrownBy(() -> claimService.rejectClaim(claimId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.CLAIM_ALREADY_CANCELLED.getMessage());
    }
}
