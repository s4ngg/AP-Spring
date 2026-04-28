package co.kr.allpick.domain.member.dto;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 응답 DTO")
public class MemberResponseDto {

    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "test@test.com")
    private String email;

    @Schema(description = "이름", example = "김상우")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "주소", example = "인천광역시 미추홀구")
    private String address;

    @Schema(description = "멤버십 등급", example = "NORMAL")
    private MemberGrade grade;

    public static MemberResponseDto from(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhone(),
                member.getAddress(),
                member.getGrade()
        );
    }
}