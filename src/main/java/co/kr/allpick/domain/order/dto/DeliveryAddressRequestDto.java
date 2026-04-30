package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.DeliveryAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다. (예: 01012345678)")
    @Schema(description = "연락처", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
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