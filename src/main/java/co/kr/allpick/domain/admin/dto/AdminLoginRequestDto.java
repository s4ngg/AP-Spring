package co.kr.allpick.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "관리자 로그인 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Schema(description = "이메일", example = "admin@allpick.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "Admin1234!@", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
