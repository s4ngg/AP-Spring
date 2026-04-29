package co.kr.allpick.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "회원 정보 수정 요청 DTO")
public class MemberUpdateRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다. (예: 01012345678)")
    @Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "주소는 필수입니다.")
    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;
}
