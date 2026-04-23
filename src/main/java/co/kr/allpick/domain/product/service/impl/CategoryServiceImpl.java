package co.kr.allpick.domain.product.service.impl;

import co.kr.allpick.domain.product.dto.CategoryProductResponseDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import co.kr.allpick.domain.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final ProductRepository productRepository;

    @Override
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