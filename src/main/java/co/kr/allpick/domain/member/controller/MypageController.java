package co.kr.allpick.domain.member.controller;

import co.kr.allpick.domain.member.docs.MypageControllerDocs;
import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.mypage.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.mypage.PasswordChangeRequestDto;
import co.kr.allpick.domain.member.service.MemberService;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MypageController implements MypageControllerDocs {

    private final MemberService memberService;

    @Override
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDto>> updateMember(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid MemberUpdateRequestDto request) {
        return ApiResponse.success("회원 정보 수정 성공", memberService.updateMember(userInfo.getMemberId(), request));
    }

    @Override
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal JwtUserInfoDto userInfo,
            @RequestBody @Valid PasswordChangeRequestDto request) {
        memberService.changePassword(userInfo.getMemberId(), request);
        return ApiResponse.success("비밀번호 변경 성공");
    }

    @Override
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMember(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        memberService.deleteMember(userInfo.getMemberId());
        return ApiResponse.success("회원 탈퇴 성공");
    }

    @Override
    @GetMapping("/me/orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getMyOrders(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("주문 목록 조회 성공", memberService.getMyOrders(userInfo.getMemberId()));
    }
}
