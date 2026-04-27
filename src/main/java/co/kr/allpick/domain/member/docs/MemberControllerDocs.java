package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.global.config.JwtUserInfoDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 정보 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "회원 정보 조회 성공",
                    "data": {
                        "id": 1,
                        "email": "test@test.com",
                        "name": "김상우",
                        "phone": "01012345678",
                        "address": "인천광역시 미추홀구",
                        "grade": "NORMAL"
                    }
                }
            """))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": false,
                    "message": "회원을 찾을 수 없습니다.",
                    "data": null
                }
            """)))
    })
        // 수정
    ResponseEntity<ApiResponse<MemberResponseDto>> getMember(JwtUserInfoDto userInfo);
}