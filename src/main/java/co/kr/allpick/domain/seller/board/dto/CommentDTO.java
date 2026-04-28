package co.kr.allpick.domain.seller.board.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//CommentDTO.java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "댓글 DTO")
public class CommentDTO {

 private Long commentId;
 private Long boardId;

 @Schema(description = "댓글 내용", example = "확인했습니다.")
 @NotBlank(message = "댓글 내용은 필수입니다.")
 @Size(max = 255, message = "댓글은 255자 이하로 입력해주세요.")
 private String content;

 @Schema(description = "작성자", example = "홍길동")
 @Size(max = 255, message = "작성자명은 255자 이하로 입력해주세요.")
 private String author;

 private LocalDateTime createdAt;
}
