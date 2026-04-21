package co.kr.allpick.domain.product.entity;

import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ProductList")
public class ProductList extends BaseEntity {

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

    private String status;

    private String approvalStatus;

    public ProductList(Long categoryId, Long sellerId, String productName,
                   String description, Integer price, String thumbnailUrl,
                   String status, String approvalStatus) {

        this.categoryId = categoryId;
        this.sellerId = sellerId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.approvalStatus = approvalStatus;
    }

    //
    public void updateInfo(String productName, String description,
                           Integer price, String thumbnailUrl) {

        this.productName = productName;
        this.description = description;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
    

    public void updateApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}