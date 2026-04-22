package co.kr.allpick.domain.member.membership.controller;

import co.kr.allpick.domain.member.membership.controller.docs.MembershipControllerDocs;
import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.domain.member.membership.service.MembershipService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController implements MembershipControllerDocs {

    private final MembershipService membershipService;

    @Override
    @GetMapping("/history/{memberId}")
    public ResponseEntity<ApiResponse<List<MembershipHistoryResponseDto>>> getMembershipHistory(
            @PathVariable("memberId") Long memberId) {
        List<MembershipHistoryResponseDto> result = membershipService.getMembershipHistory(memberId);
        return ApiResponse.success("등급 변경 이력 조회 성공", result); // ResponseEntity.ok() 제거
    }
}