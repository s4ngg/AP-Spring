package co.kr.allpick.global.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserInfoDto {
    private Long memberId;
    private String email;
}