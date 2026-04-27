package co.kr.allpick.domain.seller.board.controller;

import java.util.List;

// ✅ 누락된 import 추가
import co.kr.allpick.domain.seller.board.dto.BoardDTO;
import co.kr.allpick.domain.seller.board.dto.ExchangeRequestDTO;
import co.kr.allpick.domain.seller.board.dto.InquiryAnswerDTO;
import co.kr.allpick.domain.seller.board.dto.RefundRequestDTO;
import co.kr.allpick.domain.seller.board.service.SellerBoardService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seller/board")
@RequiredArgsConstructor
public class SellerBoardController {

    private final SellerBoardService sellerBoardService;

    @GetMapping
    public ResponseEntity<List<BoardDTO>> getBoardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        List<BoardDTO> list = sellerBoardService.getBoardList(page, size, keyword);
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@RequestBody BoardDTO boardDTO) {
        BoardDTO created = sellerBoardService.createBoard(boardDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDTO> updateBoard(
            @PathVariable Long boardId,
            @RequestBody BoardDTO boardDTO) {

        BoardDTO updated = sellerBoardService.updateBoard(boardId, boardDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        sellerBoardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<String> processRefund(
            @PathVariable Long orderId,
            @Valid @RequestBody RefundRequestDTO refundRequestDTO) {

        sellerBoardService.processRefund(orderId, refundRequestDTO);
        return ResponseEntity.ok("환불 처리가 완료되었습니다.");
    }

    @PostMapping("/exchange/{orderId}")
    public ResponseEntity<String> processExchange(
            @PathVariable Long orderId,
            @Valid @RequestBody ExchangeRequestDTO exchangeRequestDTO) {

        sellerBoardService.processExchange(orderId, exchangeRequestDTO);
        return ResponseEntity.ok("교환 처리가 완료되었습니다.");
    }

    // ✅ "/inquiry" 슬래시 추가 + 메서드 바디 추가
    @GetMapping("/inquiry")
    public ResponseEntity<List<InquiryAnswerDTO>> getInquiryList(
            @RequestParam(defaultValue = "false") boolean unanswered) {

        return ResponseEntity.ok(sellerBoardService.getInquiryList(unanswered));
    }

    @PostMapping("/inquiry/{inquiryId}/answer")
    public ResponseEntity<String> answerInquiry(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerDTO answerDTO) {

        sellerBoardService.answerInquiry(inquiryId, answerDTO);
        return ResponseEntity.ok("답변이 등록되었습니다.");
    }

    // ✅ 메서드명 공백 제거: update InquiryAnswer → updateInquiryAnswer
    // ✅ 닫는 괄호/중괄호 정리, 백슬래시 제거
    @PutMapping("/inquiry/{inquiryId}/answer")
    public ResponseEntity<String> updateInquiryAnswer(
            @PathVariable Long inquiryId,
            @RequestBody InquiryAnswerDTO answerDTO) {

        sellerBoardService.updateInquiryAnswer(inquiryId, answerDTO);
        return ResponseEntity.ok("답변이 수정되었습니다.");
    }
}