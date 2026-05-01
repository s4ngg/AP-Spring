package co.kr.allpick.domain.order.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_addresses")
@Getter
@NoArgsConstructor
public class DeliveryAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "zip_code", nullable = false, length = 10)
    private String zipCode;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(name = "address_detail", length = 200)
    private String addressDetail;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    public DeliveryAddress(Long memberId, String recipientName, String phone,
                           String zipCode, String address, String addressDetail,
                           boolean isDefault) {
        this.memberId = memberId;
        this.recipientName = recipientName;
        this.phone = phone;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public void update(String recipientName, String phone, String zipCode,
                       String address, String addressDetail, boolean isDefault) {
        this.recipientName = recipientName;
        this.phone = phone;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }
}