package co.kr.allpick.domain.admin.member.controller;

import co.kr.allpick.domain.admin.member.controller.docs.AdminMemberControllerDocs;
import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.domain.admin.member.service.AdminMemberService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class AdminMemberController implements AdminMemberControllerDocs {

    private final AdminMemberService adminMemberService;

    // GET /api/admin/members → 구매자 전체 목록
    @GetMapping
    @Override
    public ResponseEntity<ApiResponse<List<MemberListResponseDto>>> getMembers(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo) {
        return ApiResponse.success("구매자 목록 조회 성공",
                adminMemberService.getMembers(adminInfo));
    }

    // PATCH /api/admin/members/{memberId}/status → 활성/정지 토글
    @PatchMapping("/{memberId}/status")
    @Override
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @AuthenticationPrincipal AdminJwtUserInfoDto adminInfo,
            @PathVariable Long memberId) {
        adminMemberService.toggleMemberStatus(adminInfo, memberId);
        return ApiResponse.success("회원 상태 변경 성공");
    }
}
