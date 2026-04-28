package co.kr.allpick.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.allpick.domain.product.entity.ParentCategory;

@Repository
public interface ParentCategoryRepository extends JpaRepository<ParentCategory, Long>{

}
