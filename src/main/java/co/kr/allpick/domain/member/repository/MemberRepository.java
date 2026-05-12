package co.kr.allpick.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.kr.allpick.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Page<Member> findAll(Pageable pageable);
	Optional<Member> findByEmail(String email);
	boolean existsByEmail(String email);
	Optional<Member> findByIdAndDeletedAtIsNull(Long memberId);
	List<Member> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
	
	@Query("SELECT m.email FROM Member m WHERE m.phone = :phone")
    Optional<String> findEmailByUserPhone(@Param("phone") String phone);
}
