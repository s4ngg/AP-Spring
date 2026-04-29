package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.member.dto.MemberResponseDto;
import co.kr.allpick.domain.member.dto.MemberUpdateRequestDto;
import co.kr.allpick.domain.member.dto.PasswordChangeRequestDto;
import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.member.service.MemberService;
import co.kr.allpick.domain.order.dto.OrderResponseDto;
import co.kr.allpick.domain.order.entity.Order;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final Logger logger = LogManager.getLogger(MemberServiceImpl.class);

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        logger.info("[MemberService] 회원 정보 조회 - memberId: {}", memberId);
        return memberRepository.findById(memberId)
                .map(MemberResponseDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    public MemberResponseDto updateMember(Long memberId, MemberUpdateRequestDto request) {
        logger.info("[MemberService] 회원 정보 수정 - memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.update(request.getName(), request.getPhone(), request.getAddress());
        return MemberResponseDto.from(member);
    }

    @Override
    public void changePassword(Long memberId, PasswordChangeRequestDto request) {
        logger.info("[MemberService] 비밀번호 변경 - memberId: {}", memberId);
        Member member = findMemberById(memberId);
        validateCurrentPassword(request.getCurrentPassword(), member.getPassword());
        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Override
    public void deleteMember(Long memberId) {
        logger.info("[MemberService] 회원 탈퇴 - memberId: {}", memberId);
        Member member = findMemberById(memberId);
        member.delete();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders(Long memberId) {
        logger.info("[MemberService] 회원 주문 목록 조회 - memberId: {}", memberId);
        List<Order> orders = orderRepository.findByMemberIdOrderByOrderedAtDesc(memberId);
        return orders.stream()
                .map(order -> OrderResponseDto.from(order, order.getOrderItems()))
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateCurrentPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
