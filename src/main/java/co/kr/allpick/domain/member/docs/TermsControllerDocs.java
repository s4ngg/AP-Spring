package co.kr.allpick.domain.member.docs;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Terms", description = "약관 API")
public interface TermsControllerDocs {

    @Operation(summary = "약관 목록 조회", description = "활성화된 약관 목록을 전체 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "약관 조회 성공",
                    "data": [
                        {
                            "id": 1,
                            "termsType": "TERMS_OF_USE",
                            "title": "서비스 이용약관",
                            "content": "약관 내용...",
                            "required": true,
                            "active": true
                        },
                        {
                            "id": 2,
                            "termsType": "PRIVACY_POLICY",
                            "title": "개인정보처리방침",
                            "content": "약관 내용...",
                            "required": true,
                            "active": true
                        },
                        {
                            "id": 3,
                            "termsType": "MARKETING",
                            "title": "마케팅 수신 동의",
                            "content": "약관 내용...",
                            "required": false,
                            "active": true
                        },
                        {
                            "id": 4,
                            "termsType": "LOCATION",
                            "title": "위치정보 이용약관",
                            "content": "약관 내용...",
                            "required": false,
                            "active": true
                        }
                    ]
                }
            """)))
    })
    ResponseEntity<ApiResponse<List<Terms>>> getTerms();
}