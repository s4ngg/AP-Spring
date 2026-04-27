package co.kr.allpick.domain.member.service;

import co.kr.allpick.domain.member.dto.MemberResponseDto;

public interface MemberService {
    MemberResponseDto getMember(Long memberId);
}