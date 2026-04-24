package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long categoryId;

    private Long sellerId;

    private String productName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Builder
    public Product(Long categoryId, Long sellerId, String productName,
                   String description, Integer price, String thumbnailUrl,
                   ProductStatus status, ApprovalStatus approvalStatus) {
        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.approvalStatus = approvalStatus;
    }

    public void updateInfo(String productName, String description,
                           Integer price, String thumbnailUrl) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    public void updateApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}