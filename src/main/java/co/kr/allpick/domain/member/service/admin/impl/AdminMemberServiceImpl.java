package co.kr.allpick.domain.member.service.admin.impl;

import co.kr.allpick.domain.member.dto.admin.MemberListResponseDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.admin.AdminMemberService;
import co.kr.allpick.global.config.AdminJwtUserInfoDto;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;

    @Override
    public List<MemberListResponseDto> getMembers(AdminJwtUserInfoDto adminInfo) {
        return memberRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(MemberListResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleMemberStatus(AdminJwtUserInfoDto adminInfo, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.getStatus() == 1) member.suspend();
        else member.activate();
    }
}
