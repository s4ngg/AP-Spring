package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.product.dto.ClaimCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimRejectRequestDto;
import co.kr.allpick.domain.admin.product.dto.ClaimResponseDto;
import co.kr.allpick.domain.admin.product.dto.ClaimStatusUpdateRequestDto;
import co.kr.allpick.domain.admin.product.entity.Claim;
import co.kr.allpick.domain.admin.product.repository.ClaimRepository;
import co.kr.allpick.domain.admin.product.service.ClaimService;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private static final Logger logger = LogManager.getLogger(ClaimServiceImpl.class);

    private final ClaimRepository claimRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;

    // 1. 클레임 등록
    @Override
    @Transactional
    public ClaimResponseDto createClaim(ClaimCreateRequestDto request) {
        logger.info("[ClaimService] 클레임 등록 - memberId: {}", request.getMemberId());

        if (!memberRepository.existsById(request.getMemberId())) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (!orderItemRepository.existsById(request.getOrderItemId())) {
            throw new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        Claim claim = claimRepository.save(request.toEntity());
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

        if (claim.getStatus() == Claim.ClaimStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_COMPLETED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.REJECTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }

        claim.updateStatus(request.getStatus());
        return ClaimResponseDto.from(claim);
    }

    // 6. 클레임 거부
    @Override
    @Transactional
    public ClaimResponseDto rejectClaim(Long claimId, ClaimRejectRequestDto request) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLAIM_NOT_FOUND));

        if (claim.getStatus() == Claim.ClaimStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.CLAIM_ALREADY_COMPLETED);
        }
        if (claim.getStatus() == Claim.ClaimStatus.REJECTED) {
            throw new BusinessException(ErrorCode.CLAIM_INVALID_STATUS);
        }

        claim.reject(request.getRejectReason());
        return ClaimResponseDto.from(claim);
    }
}
