package kr.hhplus.ecommerce.controller;

import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.controller.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    /**
     * 상품 목록 조회
     */
    @GetMapping
    public ApiResponse<List<ProductResponse.ProductSummary>> getProducts() {
        List<ProductResponse.ProductSummary> products = new ArrayList<>();
        products.add(new ProductResponse.ProductSummary(1L, "스마트폰", 1000000, 100));
        products.add(new ProductResponse.ProductSummary(2L, "노트북", 1500000, 50));
        products.add(new ProductResponse.ProductSummary(3L, "이어폰", 300000, 200));
        
        return ApiResponse.success(products);
    }

    /**
     * 상품 상세 조회
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse.ProductDetail> getProductDetail(
        @PathVariable long productId
    ) {
        return ApiResponse.success(
            new ProductResponse.ProductDetail(
                productId, 
                "스마트폰", 
                1000000, 
                100, 
                "최신형 스마트폰입니다. 고성능 카메라와 대용량 배터리를 탑재했습니다."
            )
        );
    }

    /**
     * 인기 판매 상품 조회
     */
    @GetMapping("/popular")
    public ApiResponse<List<ProductResponse.PopularProduct>> getPopularProducts() {
        List<ProductResponse.PopularProduct> products = new ArrayList<>();
        products.add(new ProductResponse.PopularProduct(1L, "스마트폰", 1000000, 100, 87));
        products.add(new ProductResponse.PopularProduct(3L, "이어폰", 300000, 200, 65));
        products.add(new ProductResponse.PopularProduct(5L, "스마트워치", 450000, 80, 42));
        
        return ApiResponse.success(products);
    }
} 