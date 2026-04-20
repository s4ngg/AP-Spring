package co.kr.allpick.domain.member.dto;

import co.kr.allpick.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email; 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class AuthResponseDto {

    @NotBlank(message = "토큰은 필수입니다")
    @Schema(description = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Schema(description = "사용자 이메일", example = "test@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "사용자 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    
    // 정적 팩토리 메서드
    public static AuthResponseDto of(String token, Member member) {
    	AuthResponseDto dto = new AuthResponseDto();
    	dto.token = token;
    	dto.email = member.getEmail();
    	dto.name = member.getName();
    	return dto;
    }
}