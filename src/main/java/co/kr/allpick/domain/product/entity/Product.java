package co.kr.allpick.domain.product.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "products")
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Product extends BaseEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", nullable = false)
    private ParentCategory parentCategory;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> optionList = new ArrayList<>();


    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC") // 이미지 순서 정렬 추가
    private List<ProductImage> productImageList = new ArrayList<>();

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "brand", length = 50, nullable = false)
    private String brand;

    @Column(name = "thumbnail_url", length = 500, nullable = false)
    private String thumbnailUrl;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "price", precision = 12, scale = 0, nullable = false)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "manufacturer", length = 50, nullable = false)
    private String manufacturer;

    @Column(name = "origin", length = 50, nullable = false)
    private String origin;

    @Column(name = "precaution", length = 50, nullable = false)
    private String precaution;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ON_SALE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    public enum Status {
        ON_SALE, SOLD_OUT, HIDDEN
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, SUSPENDED
    }
}