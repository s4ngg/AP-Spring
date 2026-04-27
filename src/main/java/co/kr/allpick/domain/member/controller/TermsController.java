package co.kr.allpick.domain.member.controller;

import co.kr.allpick.domain.member.entity.Terms;
import co.kr.allpick.domain.member.repository.TermsRepository;
import co.kr.allpick.domain.member.service.TermsService;
import co.kr.allpick.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermsController {

	private final TermsService termsService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Terms>>> getTerms() {
        return ApiResponse.success("약관 조회 성공", termsService.getActiveTerms());
    }
}