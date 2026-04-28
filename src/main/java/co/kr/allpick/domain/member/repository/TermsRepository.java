package co.kr.allpick.domain.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.member.entity.Terms;

public interface TermsRepository extends JpaRepository<Terms, Long> {
	List<Terms> findByIsActiveTrue();
}
