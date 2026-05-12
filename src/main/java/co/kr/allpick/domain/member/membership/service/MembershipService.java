package co.kr.allpick.domain.member.membership.service;

import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.domain.member.membership.dto.MembershipStatusResponseDto;

import java.util.List;

public interface MembershipService {

    void updateAllMemberGrades();

    List<MembershipHistoryResponseDto> getMembershipHistory(Long memberId);

    MembershipStatusResponseDto getMembershipStatus(Long memberId);
}