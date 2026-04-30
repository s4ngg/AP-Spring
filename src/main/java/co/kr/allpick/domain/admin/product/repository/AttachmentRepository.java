package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    // 문의
    List<Attachment> findByInquiryIdAndDeletedAtIsNull(Long inquiryId);

    // 교환/반품
    List<Attachment> findByClaimIdAndDeletedAtIsNull(Long claimId);
}
