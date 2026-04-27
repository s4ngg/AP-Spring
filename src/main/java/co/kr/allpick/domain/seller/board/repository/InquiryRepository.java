package co.kr.allpick.domain.seller.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.seller.board.entity.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	List<Inquiry> findByIsAnsweredFalse();

}
