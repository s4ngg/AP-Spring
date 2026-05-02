package co.kr.allpick.domain.admin.product.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.product.dto.ProductApprovalResponseDto;
import co.kr.allpick.domain.admin.product.dto.ProductRejectRequestDto;
import co.kr.allpick.domain.admin.product.entity.ProductApproval;
import co.kr.allpick.domain.admin.product.repository.ProductApprovalRepository;
import co.kr.allpick.domain.admin.product.service.AdminProductApprovalService;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
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
public class AdminProductApprovalServiceImpl implements AdminProductApprovalService {

    private static final Logger logger = LogManager.getLogger(AdminProductApprovalServiceImpl.class);

    private final ProductRepository productRepository;
    private final AdminRepository adminRepository;
    private final ProductApprovalRepository productApprovalRepository;

    @Override
    @Transactional
    public ProductApprovalResponseDto approveProduct(AdminJwtUserInfoDto adminInfo, Long productId) {
        Admin admin = getSuperAdmin(adminInfo);
        Product product = getPendingProduct(productId);

        product.approve();
        ProductApproval productApproval = productApprovalRepository.save(
                ProductApproval.approved(product, admin, ProductApproval.RequestType.REGISTER)
        );

        logger.info("[AdminProductApprovalServiceImpl] 상품 승인 완료 - actorAdminId: {}, productId: {}",
                admin.getAdminId(), productId);
        return ProductApprovalResponseDto.from(productApproval);
    }

    @Override
    @Transactional
    public ProductApprovalResponseDto rejectProduct(AdminJwtUserInfoDto adminInfo, Long productId, ProductRejectRequestDto request) {
        Admin admin = getSuperAdmin(adminInfo);
        Product product = getPendingProduct(productId);
        validateRejectReason(request.getRejectReason());

        product.reject();
        ProductApproval productApproval = productApprovalRepository.save(
                ProductApproval.rejected(product, admin, ProductApproval.RequestType.REGISTER, request.getRejectReason())
        );

        logger.info("[AdminProductApprovalServiceImpl] 상품 승인 거절 완료 - actorAdminId: {}, productId: {}",
                admin.getAdminId(), productId);
        return ProductApprovalResponseDto.from(productApproval);
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

    private Product getPendingProduct(Long productId) {
        Product product = productRepository.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getApprovalStatus() != Product.ApprovalStatus.PENDING) {
            throw new BusinessException(ErrorCode.APPROVAL_ALREADY_PROCESSED);
        }
        return product;
    }

    private void validateRejectReason(String rejectReason) {
        if (!StringUtils.hasText(rejectReason)) {
            throw new BusinessException(ErrorCode.REJECT_REASON_REQUIRED);
        }
    }
}
