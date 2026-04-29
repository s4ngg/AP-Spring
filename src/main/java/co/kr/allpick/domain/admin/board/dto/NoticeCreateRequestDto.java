package co.kr.allpick.domain.admin.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 등록/수정 요청 DTO")
public class NoticeCreateRequestDto {

    @NotBlank
    @Schema(description = "공지사항 제목", example = "서비스 점검 안내", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank
    @Schema(description = "공지사항 본문", example = "4월 30일 오전 2시~4시 서비스 점검이 진행됩니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @NotNull
    @Schema(description = "고정 공지 여부 (true: 고정, false: 일반)", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isFixed;

    @Schema(description = "공지사항 이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;
}
