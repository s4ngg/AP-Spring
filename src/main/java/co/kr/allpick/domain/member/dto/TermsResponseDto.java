package co.kr.allpick.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "약관 응답 DTO")
public class TermsResponseDto {

    @Schema(description = "약관 ID", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "약관 타입", example = "TERMS_OF_USE",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String termsType;

    @Schema(description = "약관 제목", example = "이용약관 동의",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "약관 내용", example = "본 약관은...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "필수 동의 여부", example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isRequired;
}