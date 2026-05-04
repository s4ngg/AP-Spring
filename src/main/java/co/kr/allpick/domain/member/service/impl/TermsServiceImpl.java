package co.kr.allpick.domain.member.service.impl;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.domain.member.repository.TermsRepository;
import co.kr.allpick.domain.member.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsServiceImpl implements TermsService {

    private final TermsRepository termsRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Terms> getActiveTerms() {
        return termsRepository.findByIsActiveTrue();
    }
}