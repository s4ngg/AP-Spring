package co.kr.allpick.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.Product;

// 상품 레포지토리
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	// 상품명 포함 검색을 위한 메서드
	//	1. 상품명이 검색란에 들어온 경우 -> 해당 이름을 포함하는 상품의 리스트 반환
	//	2. 검색란이 비어 있을 떄 -> 모든 상품을 최신 등록순으로 불러옴.
	@Query("SELECT p from Product p " +
			"where :productName IS NULL OR :productName ='' OR p.productName LIKE %:productName% "+ 
			"ORDER BY p.createdAt DESC")
	List<Product> findByProductNameContaining(@Param("productName")String productName);
	
	// 상품 존재여부 확인 ( 상품명으로 )
	boolean existsByProductName(String productName);
}
