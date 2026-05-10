package co.kr.allpick.domain.admin.order.service;

import co.kr.allpick.domain.admin.order.dto.AdminOrderResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminOrderService {

    List<AdminOrderResponseDto> getOrders(AdminJwtUserInfoDto adminInfo);
}
