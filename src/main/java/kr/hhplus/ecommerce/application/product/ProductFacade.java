package kr.hhplus.ecommerce.application.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final OrderService orderService;
    private final ProductService productService;
    
    @Transactional(readOnly = true)
    public List<ProductVo> findTopSellingProducts(int limit) {
        List<Long> topProductIds = orderService.findTopSellingProductIds(limit);
        return productService.findAllById(topProductIds);
    }
}