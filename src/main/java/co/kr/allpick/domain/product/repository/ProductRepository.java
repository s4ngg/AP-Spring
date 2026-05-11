package co.kr.allpick.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 상품 상세 조회 - 판매중, 승인된, 삭제 안 된 상품만
    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.parentCategory " +
           "JOIN FETCH p.seller s " +
           "JOIN s.member m " +
           "WHERE p.productId = :id " +
           "AND p.status = 'ON_SALE' " +
           "AND p.approvalStatus = 'APPROVED' " +
           "AND p.deletedAt IS NULL " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    Optional<Product> findValidProduct(@Param("id") Long productId);

    // 상품명 중복 확인 (삭제된 상품명은 재사용 가능)
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.productName = :productName AND p.deletedAt IS NULL")
    boolean existsByProductName(@Param("productName") String productName);

    // 미사용 메서드 (deletedAt 필터 없음 - 사용 지양)
    List<Product> findBySeller_SellerId(Long sellerId);

    // 상품 단건 조회 (seller fetch-join, 삭제 안 된 상품만)
    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.productId = :productId AND p.deletedAt IS NULL")
    Optional<Product> findByProductIdAndDeletedAtIsNull(@Param("productId") Long productId);

    // 상품 수정/삭제용 - 판매자 본인 상품 단건 조회 (삭제 안 된 상품만)
    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.seller s " +
           "JOIN s.member m " +
           "WHERE p.productId = :productId " +
           "AND m.id = :memberId " +
           "AND p.deletedAt IS NULL " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    Optional<Product> findByProductIdAndMemberId(@Param("memberId") Long memberId, @Param("productId") Long productId);

    // 판매자 본인 상품 목록 조회 (삭제 안 된 상품만)
    @Query("SELECT p FROM Product p WHERE p.seller.sellerId = :sellerId AND p.deletedAt IS NULL")
    List<Product> findBySellerIdAndDeletedAtIsNull(@Param("sellerId") Long sellerId);

    // 관리자 상품 목록 조회
    @EntityGraph(attributePaths = {"parentCategory", "optionList", "seller"})
    List<Product> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

    // 판매 중이고 승인된 상품 목록 페이지 조회
    @EntityGraph(attributePaths = {"parentCategory"})
    @Query("SELECT p FROM Product p " +
           "JOIN p.seller s " +
           "JOIN s.member m " +
           "WHERE p.status = :status " +
           "AND p.approvalStatus = :approvalStatus " +
           "AND p.deletedAt IS NULL " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    Page<Product> findVisibleProducts(
            @Param("status") Product.Status status,
            @Param("approvalStatus") Product.ApprovalStatus approvalStatus,
            Pageable pageable
    );

    Page<Product> findByStatusAndApprovalStatusAndDeletedAtIsNull(
            Product.Status status,
            Product.ApprovalStatus approvalStatus,
            Pageable pageable
    );
}
