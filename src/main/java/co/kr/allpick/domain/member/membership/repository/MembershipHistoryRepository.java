package co.kr.allpick.domain.member.membership.repository;

import co.kr.allpick.domain.member.membership.entity.MembershipHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, Long> {

    List<MembershipHistory> findByMemberIdOrderByChangedAtDesc(Long memberId);
}