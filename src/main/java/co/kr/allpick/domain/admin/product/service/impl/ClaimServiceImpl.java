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

    // Î∞òÌíà ?ÑÏö© ?¨Ïú† (ÍµêÌôò ?îÏ≤≠ ???¨Ïö© Î∂àÍ?)
    private static final Set<Claim.ReasonCode> RETURN_ONLY_REASONS = Set.of(
            Claim.ReasonCode.CHANGE_MIND,
            Claim.ReasonCode.SIZE_COLOR,
            Claim.ReasonCode.DESCRIPTION_DIFF
    );

    // ÍµêÌôò ?ÑÏö© ?¨Ïú† (Î∞òÌíà ?îÏ≤≠ ???¨Ïö© Î∂àÍ?)
    private static final Set<Claim.ReasonCode> EXCHANGE_ONLY_REASONS = Set.of(
            Claim.ReasonCode.SIZE_CHANGE,
            Claim.ReasonCode.COLOR_CHANGE
    );

    // ?àÏö©???ÅÌÉú ?ÑÏù¥ Í∑úÏπô: SUBMITTED ??IN_PROGRESS ??COMPLETED
    private static final Map<Claim.ClaimStatus, Set<Claim.ClaimStatus>> ALLOWED_TRANSITIONS = Map.of(
            Claim.ClaimStatus.SUBMITTED, Set.of(Claim.ClaimStatus.IN_PROGRESS),
            Claim.ClaimStatus.IN_PROGRESS, Set.of(Claim.ClaimStatus.COMPLETED)
    );

    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;

    // 1. ?¥Î†à???±Î°ù
    @Override
    @Transactional
    public ClaimResponseDto createClaim(Long memberId, ClaimCreateRequestDto request) {
        logger.info("[ClaimService] ?¥Î†à???±Î°ù - memberId: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // ?†Ï≤≠ ?†ÌòïÍ≥??¨Ïú† ÏΩîÎìú Ï°∞Ìï© Í≤ÄÏ¶?
        if (request.getClaimType() == Claim.ClaimType.RETURN &&
                EXCHANGE_ONLY_REASONS.contains(request.getReasonCode())) {
            throw new BusinessException(ErrorCode.CLAIM_REASON_MISMATCH);
        }
        if (request.getClaimType() == Claim.ClaimType.EXCHANGE &&
                RETURN_ONLY_REASONS.contains(request.getReasonCode())) {
            throw new BusinessException(ErrorCode.CLAIM_REASON_MISMATCH);
        }
        // OrderItem ?åÏú†Í∂åÍπåÏßÄ ?ïÏù∏
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

    // 2. ?¥Î†à???ÅÏÑ∏ Ï°∞Ìöå
    @Override
    @Transactional(readOnly = true)
    public ClaimResponseDto getClaimById(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));
        return ClaimResponseDto.from(claim);
    }

    // 3. ???¥Î†à??Î™©Î°ù Ï°∞Ìöå
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getMyClaims(Long memberId) {
        return claimRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }

    // 4. ?ÑÏ≤¥ ?¥Î†à??Î™©Î°ù Ï°∞Ìöå
    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponseDto> getAllClaims() {
        return claimRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }

    // 5. ?¥Î†à???ÅÌÉú Î≥ÄÍ≤?
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

    // 6. ?¥Î†à??Ï∑®ÏÜå
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

    // 7. ?¥Î†à??Í±∞Î?
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
 // ??8. ?¥Î†à???πÏù∏ (?êÎß§?? SUBMITTED ??IN_PROGRESS
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
        logger.info("[ClaimService] ?êÎß§???¥Î†à???πÏù∏ - claimId: {}, memberId: {}", claimId, memberId);
        return ClaimResponseDto.from(claim);
    }

    // ??9. ?¥Î†à??Í±∞Î? (?êÎß§??
    @Override
    @Transactional
    public ClaimResponseDto rejectClaimBySeller(Long claimId, ClaimRejectRequestDto request, Long memberId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        validateSellerClaimOwnership(claim, memberId);
        validateRejectableStatus(claim);

        claim.reject(request.getRejectReason());
        logger.info("[ClaimService] ?êÎß§???¥Î†à??Í±∞Î? - claimId: {}, memberId: {}", claimId, memberId);
        return ClaimResponseDto.from(claim);
    }

    private void validateSellerClaimOwnership(Claim claim, Long memberId) {
        // ?êÎß§???±Î°ù ?¨Î? Î®ºÏ? ?ïÏù∏
        if (!sellerRepository.existsByMember_IdAndDeletedAtIsNull(memberId)) {
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
        // ?êÎß§???¨Î? + APPROVED ?ÅÌÉú ?ïÏù∏
        Seller seller = sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        if (seller.getStatus() != SellerStatus.APPROVED) {
            throw new BusinessException(ErrorCode.NOT_SELLER);
        }

        // ?êÎß§???ïÏù∏ ???¥Î†à??Ï°∞Ìöå
        return claimRepository.findBySellerIdAndDeletedAtIsNull(seller.getSellerId())
                .stream()
                .map(ClaimResponseDto::from)
                .toList();
    }
}
