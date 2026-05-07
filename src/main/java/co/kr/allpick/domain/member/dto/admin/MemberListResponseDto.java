package co.kr.allpick.domain.member.dto.admin;

import co.kr.allpick.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class MemberListResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Integer status;   // 1=활성, 0=정지
    private LocalDateTime createdAt;

    public static MemberListResponseDto from(Member member) {
        return MemberListResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
