package co.kr.allpick.domain.member.service;

import co.kr.allpick.domain.member.entity.Terms;
import java.util.List;

public interface TermsService {
    List<Terms> getActiveTerms();
}