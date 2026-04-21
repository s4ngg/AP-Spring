package co.kr.allpick.domain.product.service;

import co.kr.allpick.domain.product.dto.ProductListResponseDto;
import co.kr.allpick.domain.product.entity.Product;
import co.kr.allpick.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductListService {

    private final ProductRepository productRepository;

    public List<ProductListResponseDto> getProductList() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductListResponseDto(
                        product.getProductId(),
                        product.getProductName(),
                        product.getPrice(),
                        product.getThumbnailUrl()
                ))
                .toList();
    }
}