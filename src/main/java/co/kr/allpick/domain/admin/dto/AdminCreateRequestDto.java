package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관리자 등록 요청 DTO")
public class AdminCreateRequestDto {

    @Schema(description = "관리자 이메일", example = "admin@allpick.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "관리자 비밀번호 (최소 8자, 영문+숫자+특수문자)", example = "Admin1234!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자, 영문+숫자+특수문자 조합이어야 합니다."
    )
    private String password;

    @Schema(description = "관리자 이름", example = "이소정", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "관리자 이름은 필수입니다.")
    @Size(max = 50, message = "관리자 이름은 50자 이하여야 합니다.")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "01012341234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다. (예: 01012341234)")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String adminPhone;

    @Schema(description = "관리자 권한", example = "SUPER_ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "관리자 권한은 필수입니다.")
    private Admin.AdminRole role;

    public Admin toEntity(String encodedPassword) {
        return Admin.builder()
                .adminName(this.adminName)
                .email(this.email)
                .password(encodedPassword)
                .adminPhone(this.adminPhone)
                .role(this.role)
                .status(Admin.AdminStatus.ACTIVE)
                .build();
    }
}
