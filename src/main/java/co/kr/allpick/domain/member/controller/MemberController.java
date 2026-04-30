package co.kr.allpick.domain.member.controller;

import co.kr.allpick.domain.member.docs.MemberControllerDocs;
import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.service.MemberService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponseDto>> getMember(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("회원 정보 조회 성공", memberService.getMember(userInfo.getMemberId()));
    }
}
