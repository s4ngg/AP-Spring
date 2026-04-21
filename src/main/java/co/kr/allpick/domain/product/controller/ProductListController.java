package co.kr.allpick.domain.product.controller;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
@RestController
public class ProductListController {

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록 페이지에 노출할 상품 리스트를 조회합니다."
    )
    @GetMapping("/api/products")
    public ResponseEntity<ApiResponse<List<ProductListResponseDto>>> getProducts() {

        List<ProductListResponseDto> products = List.of(
                new ProductListResponseDto(1L, "모던 침대 프레임", 329000, "https://via.placeholder.com/300"),
                new ProductListResponseDto(2L, "프리미엄 디퓨저", 39000, "https://via.placeholder.com/300"),
                new ProductListResponseDto(3L, "무드 스탠드 조명", 35000, "https://via.placeholder.com/300")
        );

        return ApiResponse.success("상품 목록 조회 성공", products);
    }
}