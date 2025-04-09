package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse.ProductSummary>> getAllProducts() {
        List<ProductVo> products = productService.findAll();
        List<ProductResponse.ProductSummary> response = products.stream()
            .map(ProductResponse.ProductSummary::from)
            .toList();
        return ApiResponse.success(response);
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse.ProductDetails> getProduct(@PathVariable long productId) {
        ProductVo productVo = productService.findById(productId);
        return ApiResponse.success(
            ProductResponse.ProductDetails.from(productVo)
        );
    }
} 