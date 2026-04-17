package co.kr.allpick.domain.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.kr.allpick.domain.cart.dto.ProductReqDto;
import co.kr.allpick.domain.cart.dto.ProductResDto;
import co.kr.allpick.domain.cart.entity.Product;
import co.kr.allpick.domain.cart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepository productRepository;
	
	
	@Override		// 상품 등록	: 요청온거 등록 후, 응답 반환
	public ProductResDto createProduct(ProductReqDto reqDto) {
		// 요청 들어온거 엔티티 객체로 만들기
		Product product = Product.ToEntity(reqDto);
		// 만든 엔티티 객체를 실제로 저장
		Product savedProduct = productRepository.save(product);
		// 엔티티 객체를 응답객체로 변환 후, 반환
		return ProductResDto.fromEntity(savedProduct); 
	}

	@Override		// 상품 조회 (전체)		: 	전체 조회 후 응답 반환	
	public List<ProductResDto> searchProducts() {
		// 전체 상품을 조회하기 (리스트 상태).
		List<Product> products = productRepository.findAll();
		// 조회한 상품을 낱개로 분할 후 -> 응답객체 형태로 변환하고 -> 리스트로 다시 묶기
		return products.stream()
				.map(product -> ProductResDto.fromEntity(product))
				.toList();
	} 
 
	@Override		// 상품 조회 (단건)
	public ProductResDto searchProduct(Long productId) {
		Optional<Product> result = productRepository.findById(productId);
		return result
				.map( product -> ProductResDto.fromEntity(product))
				.orElseThrow(() -> new IllegalArgumentException());
	}
 
	@Override		// 상품 삭제 (전체)
	public void deleteProducts() {
		// TODO Auto-generated method stub
		
	}

	@Override		// 상품 삭제 (단건)
	public void deleteProduct(Long productId) {
		// TODO Auto-generated method stub
		
	}
	
}
