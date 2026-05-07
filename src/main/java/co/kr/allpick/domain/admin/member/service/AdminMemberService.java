package co.kr.allpick.domain.admin.member.service;

import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;

import java.util.List;

public interface AdminMemberService {

    List<MemberListResponseDto> getMembers(AdminJwtUserInfoDto adminInfo);

    void toggleMemberStatus(AdminJwtUserInfoDto adminInfo, Long memberId);
}
