package co.kr.allpick.domain.admin.product.dto;

import co.kr.allpick.domain.admin.product.entity.Attachment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "첨부파일 응답 DTO")
public class AttachmentResponseDto {

    @Schema(description = "첨부파일 ID", example = "1")
    private Long attachmentId;

    @Schema(description = "이미지 URL", example = "https://bucket.s3.amazonaws.com/inquiry/uuid_image.jpg")
    private String imageUrl;

    @Schema(description = "정렬 순서", example = "0")
    private int sortOrder;

    @Schema(description = "첨부 대상 유형", example = "INQUIRY")
    private Attachment.TargetType targetType;

    public static AttachmentResponseDto from(Attachment attachment) {
        return AttachmentResponseDto.builder()
                .attachmentId(attachment.getAttachmentId())
                .imageUrl(attachment.getImageUrl())
                .sortOrder(attachment.getSortOrder())
                .targetType(attachment.getTargetType())
                .build();
    }
}
