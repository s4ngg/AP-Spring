package co.kr.allpick.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "비밀번호 변경 요청 DTO")
public class PasswordChangeRequestDto {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    @Schema(description = "현재 비밀번호", example = "OldPass1!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
        message = "비밀번호는 최소 8자 이상, 영문·숫자·특수문자를 포함해야 합니다."
    )
    @Schema(description = "새 비밀번호 (최소 8자, 영문+숫자+특수문자)", example = "NewPass1!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
