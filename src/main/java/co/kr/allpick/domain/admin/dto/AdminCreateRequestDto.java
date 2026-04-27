package co.kr.allpick.domain.admin.dto;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.service.AdminServiceImpl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequestDto {

    @Schema(description = "이메일", example = "test@test.com")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "QWWEqwer1234!@#$")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "관리자 이름", example = "홍길동")
    @NotBlank(message = "관리자 이름은 필수입니다.")
    private String adminName;

    @Schema(description = "관리자 연락처", example = "01012341234")
    @NotBlank(message = "전화번호는 필수입니다.")
    private String adminPhone;

    @Schema(description = "관리자 연락처", example = "01012341234")
    @Pattern(regexp = "SUPER_ADMIN|CS_ADMIN", message ="role은 SUPER_ADMIN 또는 CS_ADMIN이어야 합니다.")
    private String role;


  public Admin toEntity(String encodedPassword){
      return Admin.builder()
              .adminName(this.getAdminName())
              .email(this.getEmail())
              .password( encodedPassword) //암호화된 비밀번호 저장
              .adminPhone(this.getAdminPhone())
              .role(Admin.AdminRole.valueOf(this.getRole()))
              .status(Admin.AdminStatus.ACTIVE)
              .build();


  }
}

