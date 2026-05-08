package co.kr.allpick.domain.admin.order.controller.docs;

import co.kr.allpick.domain.admin.order.dto.AdminOrderResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Admin Order", description = "관리자 주문 관리 API")
public interface AdminOrderControllerDocs {

    @Operation(summary = "관리자 주문 목록 조회", description = "SUPER_ADMIN이 전체 주문 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음")
    })
    ResponseEntity<ApiResponse<List<AdminOrderResponseDto>>> getOrders(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo);
}
