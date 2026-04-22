package co.kr.allpick.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.kr.allpick.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Page<Member> findAll(Pageable pageable);
	Optional<Member> findByEmail(String email);
	boolean existsByEmail(String email);
}
