// BoardDTO.java
package co.kr.allpick.domain.seller.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 DTO")
public class BoardDTO {

    private Long boardId;

    @Schema(description = "제목", example = "공지사항입니다.")
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")  // ✅ varchar(255)
    private String title;

    @Schema(description = "내용", example = "게시글 내용입니다.")
    @NotBlank(message = "내용은 필수입니다.")
    // TEXT 타입이므로 실용적 상한선 설정
    @Size(max = 5000, message = "내용은 5000자 이하로 입력해주세요.")  // ✅ TEXT
    private String content;

    @Schema(description = "판매자명", example = "홍길동")
    @Size(max = 255, message = "판매자명은 255자 이하로 입력해주세요.")  // ✅ varchar(255)
    private String sellerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}