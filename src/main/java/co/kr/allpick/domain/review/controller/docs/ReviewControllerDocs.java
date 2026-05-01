package co.kr.allpick.domain.review.controller.docs;

import co.kr.allpick.domain.review.dto.ReviewRequestDto;
import co.kr.allpick.domain.review.dto.ReviewResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Review", description = "리뷰 API")
public interface ReviewControllerDocs {

    @Operation(summary = "상품별 리뷰 조회", description = "특정 상품의 전체 리뷰 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": true,
                        "message": "해당 상품 리뷰를 조회합니다.",
                        "data": [
                            {
                                "reviewId": 1,
                                "writerName": "홍길동",
                                "productName": "올픽 티셔츠",
                                "selectedOption": "L / White",
                                "rating": 5,
                                "content": "너무 마음에 들어요!"
                            }
                        ]
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<List<ReviewResponseDto>>> getReviewAll(
            @PathVariable("productId") Long productId);

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
                            "productName": "올픽 티셔츠",
                            "selectedOption": "L / White",
                            "rating": 5,
                            "content": "배송도 빠르고 재질도 좋네요."
                        }
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "중복 리뷰 작성 시도",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "이미 리뷰를 작성한 상품입니다.",
                        "data": null
                    }
                """)
            )),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 내역 없음",
            content = @Content(
                examples = @ExampleObject(value = """
                    {
                        "success": false,
                        "message": "주문 내역을 찾을 수 없습니다.",
                        "data": null
                    }
                """)
            ))
    })
    ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewRequestDto reqdto);
}