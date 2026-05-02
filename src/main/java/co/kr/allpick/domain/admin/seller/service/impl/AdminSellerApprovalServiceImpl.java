package co.kr.allpick.domain.admin.seller.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.admin.seller.dto.SellerApprovalResponseDto;
import co.kr.allpick.domain.admin.seller.dto.SellerRejectRequestDto;
import co.kr.allpick.domain.admin.seller.entity.SellerApproval;
import co.kr.allpick.domain.admin.seller.repository.SellerApprovalRepository;
import co.kr.allpick.domain.admin.seller.service.AdminSellerApprovalService;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AdminSellerApprovalServiceImpl implements AdminSellerApprovalService {

    private static final Logger logger = LogManager.getLogger(AdminSellerApprovalServiceImpl.class);

    private final SellerRepository sellerRepository;
    private final AdminRepository adminRepository;
    private final SellerApprovalRepository sellerApprovalRepository;

    @Override
    @Transactional
    public SellerApprovalResponseDto approveSeller(AdminJwtUserInfoDto adminInfo, Long sellerId) {
        Admin admin = getSuperAdmin(adminInfo);
        Seller seller = getPendingSeller(sellerId);

        seller.approve();
        SellerApproval sellerApproval = sellerApprovalRepository.save(
                SellerApproval.approved(seller, admin, SellerApproval.RequestType.REGISTER)
        );

        logger.info("[AdminSellerApprovalServiceImpl] 판매자 승인 완료 - actorAdminId: {}, sellerId: {}",
                admin.getAdminId(), sellerId);
        return SellerApprovalResponseDto.from(sellerApproval);
    }

    @Override
    @Transactional
    public SellerApprovalResponseDto rejectSeller(AdminJwtUserInfoDto adminInfo, Long sellerId, SellerRejectRequestDto request) {
        Admin admin = getSuperAdmin(adminInfo);
        Seller seller = getPendingSeller(sellerId);
        validateRejectReason(request.getRejectReason());

        seller.reject();
        SellerApproval sellerApproval = sellerApprovalRepository.save(
                SellerApproval.rejected(seller, admin, SellerApproval.RequestType.REGISTER, request.getRejectReason())
        );

        logger.info("[AdminSellerApprovalServiceImpl] 판매자 승인 거절 완료 - actorAdminId: {}, sellerId: {}",
                admin.getAdminId(), sellerId);
        return SellerApprovalResponseDto.from(sellerApproval);
    }

    private Admin getSuperAdmin(AdminJwtUserInfoDto adminInfo) {
        if (adminInfo == null || adminInfo.getRole() != Admin.AdminRole.SUPER_ADMIN) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }
        Admin admin = adminRepository.findById(adminInfo.getAdminId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        if (admin.getRole() != Admin.AdminRole.SUPER_ADMIN || admin.getStatus() != Admin.AdminStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }
        return admin;
    }

    private Seller getPendingSeller(Long sellerId) {
        Seller seller = sellerRepository.findBySellerIdAndDeletedAtIsNull(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SELLER_NOT_FOUND));
        if (seller.getStatus() != SellerStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        return seller;
    }

    private void validateRejectReason(String rejectReason) {
        if (!StringUtils.hasText(rejectReason)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
    }
}
