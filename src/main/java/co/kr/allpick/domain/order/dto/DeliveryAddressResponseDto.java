package co.kr.allpick.domain.order.dto;

import co.kr.allpick.domain.order.entity.DeliveryAddress;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "배송지 응답 DTO")
public class DeliveryAddressResponseDto {

    @Schema(description = "배송지 ID", example = "1")
    private Long addressId;

    @Schema(description = "수령인 이름", example = "홍길동")
    private String recipientName;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String phone;

    @Schema(description = "우편번호", example = "12345")
    private String zipCode;

    @Schema(description = "주소", example = "서울시 강남구")
    private String address;

    @Schema(description = "상세주소", example = "101호")
    private String addressDetail;

    @Schema(description = "기본 배송지 여부", example = "false")
    private boolean isDefault;
    
    public static DeliveryAddressResponseDto from(DeliveryAddress address) {
        return DeliveryAddressResponseDto.builder()
                .addressId(address.getAddressId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .zipCode(address.getZipCode())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .isDefault(address.isDefault())
                .build();
    }
}