package co.kr.allpick.domain.seller.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.allpick.domain.seller.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByBoard_BoardId(Long boardId);

}
