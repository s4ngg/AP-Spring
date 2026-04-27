package co.kr.allpick.domain.member.membership.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.membership.dto.MembershipHistoryResponseDto;
import co.kr.allpick.domain.member.membership.entity.MembershipHistory;
import co.kr.allpick.domain.member.membership.repository.MembershipHistoryRepository;
import co.kr.allpick.domain.member.membership.service.MembershipService;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private static final Logger logger = LogManager.getLogger(MembershipServiceImpl.class);

    private final MemberRepository memberRepository;
    private final MembershipHistoryRepository membershipHistoryRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void updateAllMemberGrades() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        Map<Long, BigDecimal> amountByMember = getLastMonthAmountByMember(lastMonth);

        int page = 0;
        int size = 100;
        int updatedCount = 0;

        while (true) {
            Page<Member> memberPage = memberRepository.findAll(PageRequest.of(page, size));
            if (memberPage.isEmpty()) break;

            for (Member member : memberPage.getContent()) {
                BigDecimal totalAmount = amountByMember.getOrDefault(member.getId(), BigDecimal.ZERO);
                MemberGrade newGrade = MemberGrade.from(totalAmount.longValue());
                MemberGrade currentGrade = member.getGrade();

                if (!currentGrade.equals(newGrade)) {
                    member.updateGrade(newGrade);
                    membershipHistoryRepository.save(
                            MembershipHistory.of(member.getId(), currentGrade, newGrade, totalAmount)
                    );
                    logger.info("등급 변경 - memberId: {}, {} → {}, 구매금액: {}",
                            member.getId(), currentGrade, newGrade, totalAmount);
                    updatedCount++;
                }
            }

            if (memberPage.isLast()) break;
            page++;
        }

        logger.info("멤버십 등급 갱신 완료 - 기준월: {}, 변경: {}명", lastMonth, updatedCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipHistoryResponseDto> getMembershipHistory(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        return membershipHistoryRepository.findByMemberIdOrderByChangedAtDesc(memberId)
                .stream()
                .map(MembershipHistoryResponseDto::from)
                .collect(Collectors.toList());
    }

    // ── private 헬퍼 ──────────────────────────────────────────

    private Map<Long, BigDecimal> getLastMonthAmountByMember(YearMonth lastMonth) {
        LocalDateTime start = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime end = lastMonth.atEndOfMonth().plusDays(1).atStartOfDay();

        List<Object[]> results = orderRepository.sumDeliveredAmountByMemberBetween(start, end);

        Map<Long, BigDecimal> amountMap = new HashMap<>();
        for (Object[] row : results) {
            Long memberId = (Long) row[0];
            BigDecimal amount = new BigDecimal(row[1].toString());
            amountMap.put(memberId, amount);
        }
        return amountMap;
    }
}