package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "약관", description = "약관 관련 API")
public interface TermsControllerDocs {

    @Operation(summary = "활성 약관 목록 조회")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<ApiResponse<List<Terms>>> getTerms();
}