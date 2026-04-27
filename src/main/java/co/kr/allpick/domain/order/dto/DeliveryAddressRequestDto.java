package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.DeliveryAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

@Schema(description = "배송지 요청 DTO")
public class DeliveryAddressRequestDto {

    @NotBlank
    @Schema(description = "수령인 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientName;

    @NotBlank
    @Schema(description = "연락처", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank
    @Schema(description = "우편번호", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipCode;

    @NotBlank
    @Schema(description = "주소", example = "서울시 강남구", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Schema(description = "상세주소", example = "101호")
    private String addressDetail;

    @Schema(description = "기본 배송지 여부", example = "false")
    private boolean isDefault;

    public DeliveryAddress toEntity(Long memberId) {
        return DeliveryAddress.builder()
                .memberId(memberId)
                .recipientName(this.recipientName)
                .phone(this.phone)
                .zipCode(this.zipCode)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .isDefault(this.isDefault)
                .build();
    }
}