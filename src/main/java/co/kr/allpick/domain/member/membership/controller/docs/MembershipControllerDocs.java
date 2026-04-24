package co.kr.allpick.domain.member.membership.controller.docs;

import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Membership", description = "멤버십 등급 API")
public interface MembershipControllerDocs {

	@Operation(summary = "등급 변경 이력 조회", description = "특정 회원의 멤버십 등급 변경 이력을 조회합니다.")
	@io.swagger.v3.oas.annotations.responses.ApiResponses({
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "200", description = "조회 성공"
	    ),
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "403", description = "인증 정보 없음 또는 권한 없음"
	    ),
	    @io.swagger.v3.oas.annotations.responses.ApiResponse(
	        responseCode = "404", description = "존재하지 않는 회원"
	    )
	})
	ResponseEntity<ApiResponse<List<MembershipHistoryResponseDto>>> getMembershipHistory(Long memberId);
}