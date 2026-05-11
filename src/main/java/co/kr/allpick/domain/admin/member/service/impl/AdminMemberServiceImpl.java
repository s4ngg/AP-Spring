package co.kr.allpick.domain.admin.member.service.impl;

import co.kr.allpick.domain.admin.entity.Admin;
import co.kr.allpick.domain.admin.member.dto.MemberListResponseDto;
import co.kr.allpick.domain.admin.member.service.AdminMemberService;
import co.kr.allpick.domain.admin.repository.AdminRepository;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
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

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<MemberListResponseDto> getMembers(AdminJwtUserInfoDto adminInfo) {
        getSuperAdmin(adminInfo);
        return memberRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(MemberListResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleMemberStatus(AdminJwtUserInfoDto adminInfo, Long memberId) {
        getSuperAdmin(adminInfo);
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if (member.getStatus() == 1) member.suspend();
        else member.activate();
    }

    private Admin getSuperAdmin(AdminJwtUserInfoDto adminInfo) {
        if (adminInfo == null || adminInfo.getRole() != Admin.AdminRole.SUPER_ADMIN) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }
        Admin admin = adminRepository.findById(adminInfo.getAdminId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        if (admin.getRole() != Admin.AdminRole.SUPER_ADMIN || admin.getStatus() != Admin.AdminStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ADMIN_FORBIDDEN);
        }
        return admin;
    }
}
