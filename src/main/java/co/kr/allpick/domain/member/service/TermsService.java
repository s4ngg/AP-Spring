package co.kr.allpick.domain.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.domain.member.repository.TermsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;

    public List<Terms> getActiveTerms() {
        return termsRepository.findByIsActiveTrue();
    }
}

