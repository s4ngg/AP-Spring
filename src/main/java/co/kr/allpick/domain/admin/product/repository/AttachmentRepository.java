package co.kr.allpick.domain.admin.product.repository;

import co.kr.allpick.domain.admin.product.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByInquiryIdAndDeletedAtIsNull(Long inquiryId);

    List<Attachment> findByClaimIdAndDeletedAtIsNull(Long claimId);
}
