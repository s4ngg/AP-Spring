package co.kr.allpick.domain.seller.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_product_details")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SellerProductDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private SellerProduct product;

    @Column(name = "detail_key", nullable = false, length = 50)
    private String detailKey;   // 예: "원산지", "제조사", "유통기한"

    @Column(name = "detail_value", nullable = false, length = 500)
    private String detailValue; // 예: "국내산", "농심", "2025-12-31"

    public void update(String detailValue) {
        this.detailValue = detailValue;
    }
}