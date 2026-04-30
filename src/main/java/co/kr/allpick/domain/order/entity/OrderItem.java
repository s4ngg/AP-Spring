package co.kr.allpick.domain.order.entity;

import java.math.BigDecimal;

import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Builder
    public OrderItem(Order order, Product product,
                     BigDecimal productPrice, int quantity, BigDecimal totalPrice) {
        this.order = order;
        this.product = product;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }
}