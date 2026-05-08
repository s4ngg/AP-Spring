package co.kr.allpick.domain.admin.order.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.order.dto.AdminOrderResponseDto;
import co.kr.allpick.domain.admin.order.service.AdminOrderService;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
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
@Transactional(readOnly = true)
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final Logger logger = LogManager.getLogger(AdminOrderServiceImpl.class);

    private final AdminRepository adminRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<AdminOrderResponseDto> getOrders(AdminJwtUserInfoDto adminInfo) {
        Admin admin = getSuperAdmin(adminInfo);
        logger.info("[AdminOrderServiceImpl] 관리자 주문 목록 조회 - actorAdminId: {}", admin.getAdminId());

        return orderRepository.findAllWithMemberAndItemsOrderByOrderedAtDesc()
                .stream()
                .map(AdminOrderResponseDto::from)
                .toList();
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
}
