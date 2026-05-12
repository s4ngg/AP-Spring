package co.kr.allpick.domain.member.membership.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import co.kr.allpick.domain.member.entity.Member;
import co.kr.allpick.domain.member.entity.MemberGrade;
import co.kr.allpick.domain.member.membership.dto.MembershipStatusResponseDto;
import co.kr.allpick.domain.member.membership.entity.MembershipHistory;
import co.kr.allpick.domain.member.membership.repository.MembershipHistoryRepository;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class MembershipServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock MembershipHistoryRepository membershipHistoryRepository;
    @Mock OrderRepository orderRepository;

    @InjectMocks MembershipServiceImpl membershipService;

    @Test
    @DisplayName("전월 구매금액 30만원 이상 → SILVER 등급으로 변경")
    void updateGrade_toSilver() {
        // given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getGrade()).thenReturn(MemberGrade.NORMAL);
        when(memberRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(member)));

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{1L, new BigDecimal("350000")});
        when(orderRepository.sumDeliveredAmountByMemberBetween(any(), any()))
                .thenReturn(mockResult);
        when(membershipHistoryRepository.save(any())).thenReturn(mock(MembershipHistory.class));

        // when
        membershipService.updateAllMemberGrades();

        // then
        verify(member).updateGrade(MemberGrade.SILVER);
        verify(membershipHistoryRepository).save(any(MembershipHistory.class));
    }

    @Test
    @DisplayName("전월 구매 내역 없음 → 등급 변경 없음, 이력 저장 안 함")
    void noGradeChange_whenNoOrders() {
        // given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getGrade()).thenReturn(MemberGrade.NORMAL);
        when(memberRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(member)));

        when(orderRepository.sumDeliveredAmountByMemberBetween(any(), any()))
                .thenReturn(new ArrayList<>());

        // when
        membershipService.updateAllMemberGrades();

        // then
        verify(member, never()).updateGrade(any());
        verify(membershipHistoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("전월 구매금액 300만원 이상 → PLATINUM 등급으로 변경")
    void updateGrade_toPlatinum() {
        // given
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(2L);
        when(member.getGrade()).thenReturn(MemberGrade.GOLD);
        when(memberRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(member)));

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{2L, new BigDecimal("3500000")});
        when(orderRepository.sumDeliveredAmountByMemberBetween(any(), any()))
                .thenReturn(mockResult);
        when(membershipHistoryRepository.save(any())).thenReturn(mock(MembershipHistory.class));

        // when
        membershipService.updateAllMemberGrades();

        // then
        verify(member).updateGrade(MemberGrade.PLATINUM);
        verify(membershipHistoryRepository).save(any(MembershipHistory.class));
    }

    @Test
    @DisplayName("멤버십 현황 조회 성공 - 이번 달 구매금액 기반 다음 등급 예측")
    void getMembershipStatus_success() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "grade", MemberGrade.NORMAL);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.sumPaidAmountByMemberBetween(any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(250000));

        // when
        MembershipStatusResponseDto result = membershipService.getMembershipStatus(memberId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrentGrade()).isEqualTo("NORMAL");
        assertThat(result.getThisMonthAmount()).isEqualTo(250000L);
        assertThat(result.getPredictedGrade()).isEqualTo("NORMAL");
        assertThat(result.getNextGrade()).isEqualTo("SILVER");
        assertThat(result.getAmountToNextGrade()).isEqualTo(50000L);
    }

    @Test
    @DisplayName("멤버십 현황 조회 성공 - 이번 달 구매금액 30만원 이상 → 다음 달 SILVER 예측")
    void getMembershipStatus_predictSilver() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "grade", MemberGrade.NORMAL);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.sumPaidAmountByMemberBetween(any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(350000));

        // when
        MembershipStatusResponseDto result = membershipService.getMembershipStatus(memberId);

        // then
        assertThat(result.getPredictedGrade()).isEqualTo("SILVER");
        assertThat(result.getNextGrade()).isEqualTo("GOLD");
        assertThat(result.getAmountToNextGrade()).isEqualTo(650000L);
    }

    @Test
    @DisplayName("멤버십 현황 조회 성공 - PLATINUM 등급이면 nextGrade null")
    void getMembershipStatus_platinum_noNextGrade() {
        // given
        Long memberId = 1L;
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "grade", MemberGrade.PLATINUM);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(orderRepository.sumPaidAmountByMemberBetween(any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(5000000));

        // when
        MembershipStatusResponseDto result = membershipService.getMembershipStatus(memberId);

        // then
        assertThat(result.getPredictedGrade()).isEqualTo("PLATINUM");
        assertThat(result.getNextGrade()).isNull();
        assertThat(result.getAmountToNextGrade()).isEqualTo(0L);
    }
}