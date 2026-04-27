package co.kr.allpick.domain.seller.board.service;

import java.util.List;

import co.kr.allpick.domain.seller.board.dto.BoardDTO;
import co.kr.allpick.domain.seller.board.dto.CommentDTO;
import co.kr.allpick.domain.seller.board.dto.ExchangeRequestDTO;
import co.kr.allpick.domain.seller.board.dto.InquiryAnswerDTO;
import co.kr.allpick.domain.seller.board.dto.RefundRequestDTO;

public interface SellerBoardService {

	// 게시글
	List<BoardDTO> getBoardList(int page, int size, String keyword);
	BoardDTO getBoardDetail(Long boardId);
	BoardDTO createBoard(BoardDTO boardDTO);
	BoardDTO updateBoard(Long boardId, BoardDTO boardDTO);
	void deleteBoard(Long boardId);
	
	// 댓글
	List<CommentDTO> getComments(Long boardId);
	CommentDTO addComment(Long boardId, CommentDTO commentDTO);
	void deleteComment(Long boardId, Long commentId);
	
	// 환불/교환
	void processRefund(Long orderId, RefundRequestDTO refundRequestDTO);
	void processExchange(Long orderId, ExchangeRequestDTO exchangeRequestDTO);
	
	// 문의
	List<InquiryAnswerDTO> getInquiryList(boolean unanswered);
	void answerInquiry(Long inquiryId, InquiryAnswerDTO answerDTO);
	void updateInquiryAnswer(Long inquiryId, InquiryAnswerDTO answerDTO);
}
