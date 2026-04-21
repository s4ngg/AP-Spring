package co.kr.allpick.domain.member.dto;

import co.kr.allpick.domain.member.entity.Seller;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "판매자 회원가입 요청 DTO")
public class SignupSellerRequestDto {

    // ========== 회원 기본 정보 ==========
    @Schema(description = "이메일", example = "seller@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문/숫자/특수문자 포함)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다."
        )
    private String password;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Schema(description = "전화번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;

    @Schema(description = "주소", example = "서울시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    // ========== 판매자 추가 정보 ==========
    @Schema(description = "사업자 등록번호", example = "123-45-67890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "사업자 등록번호는 필수입니다.")
    
    private String businessNumber;
    @Schema(description = "상호명", example = "홍길동 상회", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "상호명은 필수입니다.")
    private String businessName;

    

    @Schema(description = "대표자명", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "대표자명은 필수입니다.")
    private String representativeName;

    @Schema(description = "은행명", example = "국민은행")
    private String bankName;

    @Schema(description = "계좌번호", example = "123-456-789012")
    private String bankAccount;
    
    public Seller toEntity(String encodedPassword) {
    	return Seller.builder()
    			.email(this.email)
    			.password(encodedPassword)
    			.name(this.name)
    			.phone(this.phone)
    			.address(this.address)
    			.businessNumber(this.businessNumber)
    			.businessName(this.businessName)
    			.build();
    			
    }
}