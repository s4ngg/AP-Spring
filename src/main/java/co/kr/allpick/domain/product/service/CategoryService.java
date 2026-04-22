package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProductRepository productRepository;

    public List<CategoryProductResponseDto> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);

        return products.stream()
                .map(product -> new CategoryProductResponseDto(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getThumbnailUrl()
                ))
                .toList();
    }
}