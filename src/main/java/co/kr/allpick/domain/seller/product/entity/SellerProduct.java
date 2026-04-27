package co.kr.allpick.domain.seller.product.entity;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller_products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SellerProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Category category = Category.ETC;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ON_SALE;

    
    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;  // ← 상품 간략 설명 추가
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SellerProductDetail> details = new ArrayList<>();
    
    public enum Category {
        BEAUTY,   // 뷰티
        FASHION,  // 패션
        LIVING,   // 리빙
        ETC       // 기타
    }

    public enum Status {
        ON_SALE,   // 판매중
        SOLD_OUT,  // 품절
        HIDDEN     // 숨김
    }

    // 상품 수정
    public void update(String name, Category category, Integer price,
                       Integer stock, String description,
                       String shortDescription,  // ← 추가
                       String thumbnailUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.description = description;
        this.shortDescription = shortDescription;  // ← 추가
        this.thumbnailUrl = thumbnailUrl;
    }

    // 재고 감소
    public void decreaseStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.stock -= quantity;
        if (this.stock == 0) {
            this.status = Status.SOLD_OUT;
        }
    }

    // 재고 증가
    public void increaseStock(int quantity) {
        this.stock += quantity;
        if (this.status == Status.SOLD_OUT && this.stock > 0) {
            this.status = Status.ON_SALE;
        }
    }

    // 상태 변경
    public void changeStatus(Status status) {
        this.status = status;
    }

    // 탈퇴 (Soft Delete)
    public void delete() {
        this.status = Status.HIDDEN;
        super.delete();
    }
}