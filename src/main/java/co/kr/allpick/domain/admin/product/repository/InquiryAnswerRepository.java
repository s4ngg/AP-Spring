package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {

    List<InquiryAnswer> findByInquiryId(Long inquiryId);

}
