package co.kr.allpick.domain.member.membership.controller.docs;

import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.domain.member.membership.dto.MembershipStatusResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Membership", description = "멤버십 등급 API")
public interface MembershipControllerDocs {

	@Operation(summary = "등급 변경 이력 조회", description = "특정 회원의 멤버십 등급 변경 이력을 조회합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
	})
	@GetMapping("/history/{memberId}")
	ResponseEntity<ApiResponse<List<MembershipHistoryResponseDto>>> getMembershipHistory(
			@PathVariable("memberId") Long memberId);

	@Operation(summary = "멤버십 현황 조회", description = "이번 달 구매 금액 기반 현황 및 다음 달 예상 등급을 조회합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원")
	})
	@GetMapping("/status")
	ResponseEntity<ApiResponse<MembershipStatusResponseDto>> getMembershipStatus(
			@AuthenticationPrincipal JwtUserInfoDto userInfo);
}