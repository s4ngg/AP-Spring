package co.kr.allpick.domain.seller.board.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.seller.board.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
	List<Board> findByTitleContaining(String keyword, Pageable pageble);
}
