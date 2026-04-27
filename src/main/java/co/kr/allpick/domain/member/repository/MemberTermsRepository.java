package co.kr.allpick.domain.member.repository;

import co.kr.allpick.domain.member.entity.MemberTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermsRepository extends JpaRepository<MemberTerms, Long> {
}