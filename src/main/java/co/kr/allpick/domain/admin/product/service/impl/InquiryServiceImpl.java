package co.kr.allpick.domain.admin.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import co.kr.allpick.domain.admin.product.dto.AttachmentResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryCreateRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerRequestDto;
import co.kr.allpick.domain.admin.product.dto.InquiryResponseDto;
import co.kr.allpick.domain.admin.product.dto.InquiryAnswerResponseDto;
import co.kr.allpick.domain.admin.product.entity.Inquiry;
import co.kr.allpick.domain.admin.product.entity.InquiryAnswer;
import co.kr.allpick.domain.admin.product.repository.AttachmentRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryRepository;
import co.kr.allpick.domain.admin.product.repository.InquiryAnswerRepository;
import co.kr.allpick.domain.admin.product.service.InquiryService;
import co.kr.allpick.domain.member.repository.MemberRepository;
import co.kr.allpick.domain.order.repository.OrderItemRepository;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.seller.entity.Seller;
import co.kr.allpick.domain.seller.repository.SellerRepository;
import co.kr.allpick.global.exception.BusinessException;
import co.kr.allpick.global.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private static final Logger logger = LogManager.getLogger(InquiryServiceImpl.class);

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final AttachmentRepository attachmentRepository;
    private final MemberRepository memberRepository;
    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    // 1. 문의 ?�록
    @Override
    @Transactional
    public InquiryResponseDto createInquiry(Long memberId, InquiryCreateRequestDto request) {
        logger.info("[InquiryService] 문의 ?�록 - memberId: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        if (request.getOrderItemId() != null && !orderItemRepository.existsById(request.getOrderItemId())) {
            throw new BusinessException(ErrorCode.ORDER_ITEM_NOT_FOUND);
        }

        Inquiry inquiry = inquiryRepository.save(request.toEntity(memberId));
        return InquiryResponseDto.from(inquiry, List.of(), List.of());
    }

    // 2. 문의 ?�세 조회
    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDto getInquiryById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));
        List<InquiryAnswerResponseDto> answers = inquiryAnswerRepository
                .findByInquiryId(inquiryId)
                .stream()
                .map(InquiryAnswerResponseDto::from)
                .toList();
        List<AttachmentResponseDto> attachments = attachmentRepository
                .findByInquiryIdAndDeletedAtIsNull(inquiryId)
                .stream()
                .map(AttachmentResponseDto::from)
                .toList();
        return InquiryResponseDto.from(inquiry, answers, attachments);
    }

    // 3. ??문의 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getMyInquiries(Long memberId) {
        return inquiryRepository.findByMemberIdAndDeletedAtIsNull(memberId)
                .stream()
                .map(inquiry -> InquiryResponseDto.from(inquiry, List.of(), List.of()))
                .toList();
    }

    // 4. 관리자 ?�체 문의 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getAllInquiries() {
        return inquiryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(inquiry -> InquiryResponseDto.from(inquiry, List.of(), List.of()))
                .toList();
    }

    // 5. ?�매???�체 문의 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getSellerInquiries(Long memberId) {
        Seller seller = sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));

        List<Long> productIds = productRepository.findBySellerIdAndDeletedAtIsNull(seller.getSellerId())
                .stream()
                .map(Product::getProductId)
                .toList();

        return inquiryRepository.findByProductIdInAndDeletedAtIsNull(productIds)
                .stream()
                .map(inquiry -> InquiryResponseDto.from(inquiry, List.of(), List.of()))
                .toList();
    }


    // 6. 관리자 ?��? ?�록
    @Override
    @Transactional
    public InquiryAnswerResponseDto addAdminAnswer(Long inquiryId, InquiryAnswerRequestDto request, Long adminId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));
        inquiry.updateStatus(Inquiry.InquiryStatus.PROCESSING);
        InquiryAnswer answer = inquiryAnswerRepository.save(request.toEntity(inquiryId, adminId, null));
        return InquiryAnswerResponseDto.from(answer);
    }

    // 7. ?�매???��? ?�록
    @Override
    @Transactional
    public InquiryAnswerResponseDto addSellerAnswer(Long inquiryId, InquiryAnswerRequestDto request, Long memberId) {
        Seller seller = sellerRepository.findByMember_IdAndDeletedAtIsNull(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_SELLER));
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));
        if (inquiry.getProductId() != null) {
            Product product = productRepository.findById(inquiry.getProductId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            if (!product.getSeller().getSellerId().equals(seller.getSellerId())) {
                throw new BusinessException(ErrorCode.INQUIRY_UNAUTHORIZED);
            }
        }
        inquiry.updateStatus(Inquiry.InquiryStatus.PROCESSING);
        InquiryAnswer answer = inquiryAnswerRepository.save(request.toEntity(inquiryId, null, seller.getSellerId()));
        return InquiryAnswerResponseDto.from(answer);
    }

    // 8. 문의 취소
    @Override
    @Transactional
    public void cancelInquiry(Long inquiryId, Long memberId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));
        if (!inquiry.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.INQUIRY_UNAUTHORIZED);
        }
        if (inquiry.getStatus() != Inquiry.InquiryStatus.PENDING) {
            throw new BusinessException(ErrorCode.INQUIRY_CANNOT_CANCEL);
        }
        inquiry.cancel();
    }
}
