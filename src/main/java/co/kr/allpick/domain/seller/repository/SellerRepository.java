package co.kr.allpick.domain.seller.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.entity.SellerStatus;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByMemberId(Long memberId);

    boolean existsByBusinessNumber(String businessNumber);

    Optional<Seller> findByBusinessNumber(String businessNumber);

    Optional<Seller> findBySellerIdAndDeletedAtIsNull(Long sellerId);

    boolean existsByMemberId(Long memberId);

    @Query("SELECT s FROM Seller s " +
           "JOIN FETCH s.member m " +
           "WHERE m.id = :memberId " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    Optional<Seller> findByMemberIdAndDeletedAtIsNull(@Param("memberId") Long memberId);

    @Query("SELECT s FROM Seller s " +
           "JOIN FETCH s.member m " +
           "WHERE m.id = :memberId " +
           "AND s.status = 'APPROVED' " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL " +
           "AND m.status = 1")
    Optional<Seller> findWithMemberByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(s) > 0 FROM Seller s " +
           "JOIN s.member m " +
           "WHERE m.id = :memberId " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    boolean existsByMemberIdAndDeletedAtIsNull(@Param("memberId") Long memberId);

    @Query("SELECT COUNT(s) > 0 FROM Seller s " +
           "JOIN s.member m " +
           "WHERE m.id = :memberId " +
           "AND s.status = :status " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    boolean existsByMemberIdAndStatusAndDeletedAtIsNull(
            @Param("memberId") Long memberId,
            @Param("status") SellerStatus status
    );

    @Query("SELECT s FROM Seller s " +
           "JOIN FETCH s.member m " +
           "WHERE s.sellerId = :sellerId " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL")
    Optional<Seller> findActiveMemberSellerBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT s FROM Seller s " +
           "JOIN FETCH s.member m " +
           "WHERE s.status IN :statuses " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY s.createdAt DESC")
    List<Seller> findAllActiveMemberSellersByStatusIn(@Param("statuses") List<SellerStatus> statuses);

    @Query("SELECT s FROM Seller s " +
           "JOIN FETCH s.member m " +
           "WHERE s.status = :status " +
           "AND s.deletedAt IS NULL " +
           "AND m.deletedAt IS NULL " +
           "ORDER BY s.createdAt DESC")
    List<Seller> findAllActiveMemberSellersByStatus(@Param("status") SellerStatus status);
}
