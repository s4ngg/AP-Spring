package co.kr.allpick.domain.seller.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.allpick.domain.seller.board.dto.BoardDTO;
import co.kr.allpick.domain.seller.board.dto.CommentDTO;
import co.kr.allpick.domain.seller.board.dto.InquiryAnswerDTO;
import co.kr.allpick.domain.seller.board.dto.RefundRequestDTO;
import co.kr.allpick.domain.seller.board.entity.Board;
import co.kr.allpick.domain.seller.board.entity.Comment;
import co.kr.allpick.domain.seller.board.entity.BoardInquiry;
import co.kr.allpick.domain.seller.board.dto.ExchangeRequestDTO;
import co.kr.allpick.domain.seller.board.repository.BoardRepository;
import co.kr.allpick.domain.seller.board.repository.BoardCommentRepository;
import co.kr.allpick.domain.seller.board.repository.BoardInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerBoardServiceImpl implements SellerBoardService{

	private final BoardRepository boardRepository;
	private final BoardCommentRepository commentRepository;
	private final BoardInquiryRepository inquiryRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<BoardDTO> getBoardList(int page, int size, String keyword) {
		Pageable pageable = PageRequest.of(page, size);
		
		if (keyword != null && !keyword.isBlank()) {
			return boardRepository.findByTitleContaining(keyword, pageable)
					.stream()
					.map(this::toBoardDTO)
					.collect(Collectors.toList());
		}
		return boardRepository.findAll(pageable)
				.stream()
				.map(this::toBoardDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public BoardDTO createBoard(BoardDTO boardDTO) {
		Board board = Board.builder()
				.title(boardDTO.getTitle())
				.content(boardDTO.getContent())
				.sellerName(boardDTO.getSellerName())
				.createdAt(LocalDateTime.now())
				.build();
		return toBoardDTO(boardRepository.save(board));
	}
	
	@Override
	public BoardDTO updateBoard(Long boardId, BoardDTO boardDTO) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id: " + boardId));
		board.setTitle(boardDTO.getTitle());
		board.setContent(boardDTO.getContent());
		board.setUpdatedAt(LocalDateTime.now());
		return toBoardDTO(boardRepository.save(board));
	}
	
	@Override
	public void deleteBoard(Long boardId) {
		if(!boardRepository.existsById(boardId)) {
			throw new RuntimeException("게시글을 찾을 수 없습니다. id: " + boardId);
		}
		boardRepository.deleteById(boardId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CommentDTO> getComments(Long boardId) {
		return commentRepository.findByBoard_BoardId(boardId)
				.stream()
				.map(this::toCommentDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public CommentDTO addComment(Long boardId, CommentDTO commentDTO) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(()-> new RuntimeException("게시글을 찾을 수 없습니다. id: " + boardId));
		Comment comment = Comment.builder()
				.board(board)
				.content(commentDTO.getContent())
				.author(commentDTO.getAuthor())
				.createdAt(LocalDateTime.now())
				.build();
		return toCommentDTO(commentRepository.save(comment));
	}
	
	@Override
	public void deleteComment(Long boardId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다. id: "+ commentId));
		if (!comment.getBoard().getBoardId().equals(boardId)) {
			throw new RuntimeException("해당 게시글의 댓글이 아닙니다.");
		}
		commentRepository.deleteById(commentId);
		}
	@Override
	public void processRefund(Long orderId, RefundRequestDTO refundRequestDTO) {
		
	}
	@Override
	public void processExchange(Long orderId, ExchangeRequestDTO exchangeRequestDTO) {
		
	}
	@Override
	@Transactional(readOnly = true)
	public List<InquiryAnswerDTO> getInquiryList(boolean unanswered) {
		List<BoardInquiry> inquiries = unanswered
				? inquiryRepository.findByIsAnsweredFalse()
				: inquiryRepository.findAll();
		return inquiries.stream()
				.map(this::toInquiryDTO)
				.collect(Collectors.toList());
	}
	
	@Override
	public void answerInquiry(Long inquiryId, InquiryAnswerDTO answerDTO) {
		BoardInquiry inquiry = inquiryRepository.findById(inquiryId)
				.orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다. id: " +inquiryId));
		inquiry.setAnswer(answerDTO.getAnswer());
		inquiry.setAnswered(true);
		inquiry.setAnsweredAt(LocalDateTime.now());
		inquiryRepository.save(inquiry);
	}
	@Override
	public void updateInquiryAnswer(Long inquiryId, InquiryAnswerDTO answerDTO) {
		BoardInquiry inquiry = inquiryRepository.findById(inquiryId)
				.orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다. id: " + inquiryId));
		inquiry.setAnswer(answerDTO.getAnswer());
		inquiry.setAnsweredAt(LocalDateTime.now());
		inquiryRepository.save(inquiry);
	}
	
	private BoardDTO toBoardDTO(Board board) {
		return BoardDTO.builder()
				.boardId(board.getBoardId())
				.title(board.getTitle())
				.content(board.getContent())
				.sellerName(board.getSellerName())
				.createdAt(board.getCreatedAt())
				.updatedAt(board.getUpdatedAt())
				.build();
	}
	
	private CommentDTO toCommentDTO(Comment comment) {
		return CommentDTO.builder()
				.commentId(comment.getCommentId())
				.boardId(comment.getBoard().getBoardId())
				.content(comment.getContent())
				.author(comment.getAuthor())
				.createdAt(comment.getCreatedAt())
				.build();
	}
	
	private InquiryAnswerDTO toInquiryDTO(BoardInquiry inquiry) {
		return InquiryAnswerDTO.builder()
				.inquiryId(inquiry.getInquiryId())
				.question(inquiry.getQuestion())
				.answer(inquiry.getAnswer())
				.isAnswered(inquiry.isAnswered())
				.answeredAt(inquiry.getAnsweredAt())
				.build();
	}
	// SellerBoardServiceImpl.java 에 추가
	@Override
	@Transactional(readOnly = true)
	public BoardDTO getBoardDetail(Long boardId) {
	    Board board = boardRepository.findById(boardId)
	            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id: " + boardId));
	    return toBoardDTO(board);
	}
}
