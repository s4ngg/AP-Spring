package co.kr.allpick.domain.member.service;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.mypage.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.mypage.PasswordChangeRequestDto;
import co.kr.allpick.domain.order.dto.OrderResponseDto;

import java.util.List;

public interface MemberService {

    MemberResponseDto getMember(Long memberId);

    MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto request);

    void changePassword(Long memberId, PasswordChangeRequestDto request);

    void deleteMember(Long memberId);

    List<OrderResponseDto> getMyOrders(Long memberId);
}
