package co.kr.allpick.domain.member.service.admin;

import co.kr.allpick.domain.member.dto.admin.MemberListResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminMemberService {

    List<MemberListResponseDto> getMembers(AdminJwtUserInfoDto adminInfo);

    void toggleMemberStatus(AdminJwtUserInfoDto adminInfo, Long memberId);
}
