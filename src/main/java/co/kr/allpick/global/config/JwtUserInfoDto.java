package co.kr.allpick.global.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class JwtUserInfoDto {
    private Long memberId;
    private String email;
    private String role;
}