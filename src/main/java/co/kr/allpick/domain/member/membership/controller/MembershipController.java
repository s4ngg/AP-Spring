package co.kr.allpick.domain.member.membership.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.allpick.domain.member.membership.controller.docs.MembershipControllerDocs;
import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.domain.member.membership.dto.MembershipStatusResponseDto;
import co.kr.allpick.domain.member.membership.service.MembershipService;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController implements MembershipControllerDocs {

    private final MembershipService membershipService;

    @Override
    @GetMapping("/history/{memberId}")
    public ResponseEntity<ApiResponse<List<MembershipHistoryResponseDto>>> getMembershipHistory(
            @PathVariable("memberId") Long memberId) {
        return ApiResponse.success("등급 변경 이력 조회 성공", membershipService.getMembershipHistory(memberId));
    }

    @Override
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<MembershipStatusResponseDto>> getMembershipStatus(
            @AuthenticationPrincipal JwtUserInfoDto userInfo) {
        return ApiResponse.success("멤버십 현황 조회 성공", membershipService.getMembershipStatus(userInfo.getMemberId()));
    }
}