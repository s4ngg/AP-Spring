package co.kr.allpick.domain.seller.board.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//InquiryAnswerDTO.java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 답변 DTO")
public class InquiryAnswerDTO {

 private Long inquiryId;

 @Schema(description = "문의 내용", example = "배송은 언제 되나요?")
 // TEXT 타입
 @Size(max = 5000, message = "문의 내용은 5000자 이하로 입력해주세요.")
 private String question;

 @Schema(description = "답변 내용", example = "3~5일 내로 배송됩니다.")
 // TEXT 타입
 @Size(max = 5000, message = "답변은 5000자 이하로 입력해주세요.")
 private String answer;

 private boolean isAnswered;
 private LocalDateTime answeredAt;
}
