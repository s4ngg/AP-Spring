package co.kr.allpick.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequestDto {
	private String email;
	private String password;
	private String name;
	private String phone;
}
  