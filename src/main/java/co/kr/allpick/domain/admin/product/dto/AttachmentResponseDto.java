package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "첨부파일 응답 DTO")
public class AttachmentResponseDto {

    @Schema(description = "첨부파일 ID", example = "1")
    private Long attachmentId;

    @Schema(description = "대상 유형 (INQUIRY=문의, CLAIM=클레임)", example = "INQUIRY")
    private Attachment.TargetType targetType;

    @Schema(description = "이미지 URL", example = "https://bucket.s3.amazonaws.com/inquiries/uuid.jpg")
    private String imageUrl;

    @Schema(description = "정렬 순서 (0부터)", example = "0")
    private int sortOrder;

    @Schema(description = "등록 일시", example = "2026-04-28T10:00:00")
    private LocalDateTime createdAt;

    public static AttachmentResponseDto from(Attachment attachment) {
        return AttachmentResponseDto.builder()
                .attachmentId(attachment.getAttachmentId())
                .targetType(attachment.getTargetType())
                .imageUrl(attachment.getImageUrl())
                .sortOrder(attachment.getSortOrder())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
