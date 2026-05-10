package co.kr.allpick.domain.admin.member.controller.docs;

import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Admin Member", description = "관리자 구매자 관리 API")
public interface AdminMemberControllerDocs {

    @Operation(summary = "구매자 목록 조회", description = "SUPER_ADMIN이 구매자 회원 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "구매자 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음")
    })
    ResponseEntity<ApiResponse<List<MemberListResponseDto>>> getMembers(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo);

    @Operation(summary = "구매자 상태 변경", description = "SUPER_ADMIN이 구매자 회원 상태를 활성/정지로 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "SUPER_ADMIN 권한 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<ApiResponse<Void>> toggleStatus(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @Parameter(description = "구매자 회원 ID") @PathVariable("memberId") Long memberId);
}
