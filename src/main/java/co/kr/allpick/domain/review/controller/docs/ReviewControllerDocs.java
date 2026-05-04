package co.kr.allpick.domain.review.controller.docs;

import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.domain.review.dto.ReviewUpdateRequestDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewControllerDocs {

    @Operation(summary = "상품별 리뷰 조회(페이징)", description = "특정 상품의 리뷰 목록을 페이징 처리하여 최신순으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "해당 상품 리뷰를 조회합니다.",
                        "data": {
                            "content": [
                                {
                                    "reviewId": 1,
                                    "writerName": "홍길동",
                                    "productName": "올픽 시그니처 티셔츠",
                                    "selectedOption": "L / White",
                                    "rating": 5,
                                    "content": "재질이 너무 부드럽고 핏이 예뻐요!"
                                }
                            ],
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                """)
            ))
    })
    @GetMapping("/{productId}")
    ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getReviewAll(
            @PathVariable("productId") Long productId,
            @ParameterObject @PageableDefault(size = 5, sort = "createdAt", 
                                                direction = Sort.Direction.DESC) Pageable pageable);

    @Operation(summary = "리뷰 등록", description = "주문 상품에 대한 리뷰를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 등록 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "리뷰를 등록했습니다.",
                        "data": {
                            "reviewId": 1,
                            "writerName": "홍길동",
                            "productName": "올픽 시그니처 티셔츠",
                            "rating": 5,
                            "content": "재질이 너무 부드럽고 핏이 예뻐요!"
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "중복 리뷰 작성 시도",
            content = @Content(examples = @ExampleObject(value = "{\"success\": false, \"message\": \"이미 리뷰를 작성한 상품입니다.\"}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 내역 없음",
            content = @Content(examples = @ExampleObject(value = "{\"success\": false, \"message\": \"주문 내역을 찾을 수 없습니다.\"}")))
    })
    ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewRequestDto reqdto);

    // --- 추가된 리뷰 수정(Patch) 부분 ---
    @Operation(summary = "리뷰 수정", description = "기존에 작성한 리뷰를 수정합니다. (작성자 본인만 가능)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "리뷰가 수정되었습니다.",
                        "data": {
                            "reviewId": 1,
                            "writerName": "홍길동",
                            "rating": 4,
                            "content": "수정된 리뷰 내용입니다."
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
            content = @Content(examples = @ExampleObject(value = "{\"success\": false, \"message\": \"리뷰 작성자가 아닙니다.\"}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "리뷰 없음",
            content = @Content(examples = @ExampleObject(value = "{\"success\": false, \"message\": \"리뷰를 찾을 수 없습니다.\"}")))
    })
    @PatchMapping("/{reviewId}")
    ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto reqDto);
}