package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.admin.product.service.ClaimService;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.entity.OrderItem;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private static final Logger logger = LogManager.getLogger(ClaimServiceImpl.class);

    // 반품 전용 사유 (교환 요청 시 사용 불가)
    private static final Set<Claim.ReasonCode> RETURN_ONLY_REASONS = Set.of(
            Claim.ReasonCode.CHANGE_MIND,
            Claim.ReasonCode.SIZE_COLOR,
            Claim.ReasonCode.DESCRIPTION_DIFF
    );

    // 교환 전용 사유 (반품 요청 시 사용 불가)
    private static final Set<Claim.ReasonCode> EXCHANGE_ONLY_REASONS = Set.of(
            Claim.ReasonCode.SIZE_CHANGE,
            Claim.ReasonCode.COLOR_CHANGE
    );

    // 허용된 상태 전이 규칙: SUBMITTED → IN_PROGRESS → COMPLETED
    private static final Map<Claim.ClaimStatus, Set<Claim.ClaimStatus>> ALLOWED_TRANSITIONS = Map.of(
            Claim.ClaimStatus.SUBMITTED, Set.of(Claim.ClaimStatus.IN_PROGRESS),
            Claim.ClaimStatus.IN_PROGRESS, Set.of(Claim.ClaimStatus.COMPLETED)
    );

    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;

    // 1. 클레임 등록
    @Override
    @Transactional
    public ClaimResponseDto createClaim(Long memberId, ClaimCreateRequestDto request) {
        logger.info("[ClaimService] 클레임 등록 - memberId: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 신청 유형과 사유 코드 조합 검증
        if (request.getClaimType() == Claim.ClaimType.RETURN &&
                EXCHANGE_ONLY_REASONS.contains(request.getReasonCode())) {
            throw new BusinessException(ErrorCode.CLAIM_REASON_MISMATCH);
        }
        if (request.getClaimType() == Claim.ClaimType.EXCHANGE &&
                RETURN_ONLY_REASONS.contains(request.getReasonCode())) {
            throw new BusinessException(ErrorCode.CLAIM_REASON_MISMATCH);
        }
        // OrderItem 소유권까지 확인
        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        if (!orderItem.getOrder().getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.CLAIM_UNAUTHORIZED);
        }
        if (claimRepository.existsByOrderItemIdAndStatusNotIn(
                request.getOrderItemId(),
                List.of(Claim.ClaimStatus.CANCELLED, Claim.ClaimStatus.REJECTED))) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_EXISTS);
        }

        Claim claim = claimRepository.save(request.toEntity(memberId));
        return ClaimResponseDto.from(claim);
    }

    // 2. 클레임 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimById(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));
        return ClaimResponseDto.from(claim);
    }

    // 3. 내 클레임 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getMyClaims(Long memberId) {
        return claimRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }

    // 4. 전체 클레임 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getAllClaims() {
        return claimRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }

    // 5. 클레임 상태 변경
    @Override
    @Transactional
    public ClaimResponseDto updateStatus(Long claimId, ClaimStatusUpdateRequestDto request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        if (!ALLOWED_TRANSITIONS.getOrDefault(claim.getStatus(), Set.of()).contains(request.getStatus())) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }

        claim.updateStatus(request.getStatus());
        return ClaimResponseDto.from(claim);
    }

    // 6. 클레임 취소
    @Override
    @Transactional
    public void cancelClaim(Long claimId, Long memberId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));
        if (!claim.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.CLAIM_UNAUTHORIZED);
        }
        if (claim.getStatus() != Claim.ClaimStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }
        claim.cancel();
    }

    // 7. 클레임 거부
    @Override
    @Transactional
    public ClaimResponseDto rejectClaim(Long claimId, ClaimRejectRequestDto request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        if (claim.getStatus() == Claim.ClaimStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_COMPLETED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_CANCELLED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.REJECTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }

        claim.reject(request.getRejectReason());
        return ClaimResponseDto.from(claim);
    }
 // ✅ 8. 클레임 승인 (판매자) SUBMITTED → IN_PROGRESS
    @Override
    @Transactional
    public ClaimResponseDto approveClaim(Long claimId, Long memberId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        validateSellerClaimOwnership(claim, memberId);

        if (claim.getStatus() != Claim.ClaimStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }

        claim.updateStatus(Claim.ClaimStatus.IN_PROGRESS);
        logger.info("[ClaimService] 판매자 클레임 승인 - claimId: {}, memberId: {}", claimId, memberId);
        return ClaimResponseDto.from(claim);
    }

    // ✅ 9. 클레임 거부 (판매자)
    @Override
    @Transactional
    public ClaimResponseDto rejectClaimBySeller(Long claimId, ClaimRejectRequestDto request, Long memberId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        validateSellerClaimOwnership(claim, memberId);
        validateRejectableStatus(claim);

        claim.reject(request.getRejectReason());
        logger.info("[ClaimService] 판매자 클레임 거부 - claimId: {}, memberId: {}", claimId, memberId);
        return ClaimResponseDto.from(claim);
    }

    private void validateSellerClaimOwnership(Claim claim, Long memberId) {
        // 판매자 등록 여부 먼저 확인
        if (!sellerRepository.existsByMemberIdAndDeletedAtIsNull(memberId)) {
            throw new BusinessException(ErrorCode.NOT_SELLER);
        }

        OrderItem orderItem = orderItemRepository.findByIdWithSellerMember(claim.getOrderItemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND));

        Long productOwnerMemberId = orderItem.getProduct().getSeller().getMember().getId();
        if (!productOwnerMemberId.equals(memberId)) {
            throw new BusinessException(ErrorCode.CLAIM_UNAUTHORIZED);
        }
    }

    private void validateRejectableStatus(Claim claim) {
        if (claim.getStatus() == Claim.ClaimStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_COMPLETED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_CANCELLED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.REJECTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getSellerClaims(Long memberId) {
        // 판매자 여부 + APPROVED 상태 확인
        Seller seller = sellerRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(ErrorCode.NOT_SELLER);
        }

        // 판매자 확인 후 클레임 조회
        return claimRepository.findBySellerIdAndDeletedAtIsNull(seller.getSellerId())
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }
}
