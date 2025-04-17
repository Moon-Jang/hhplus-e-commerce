package kr.hhplus.ecommerce.application.product;

import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final DailyProductSalesService dailyProductSalesService;
    
    @Transactional(readOnly = true)
    public List<ProductVo> findTopSellingProducts(int limit) {
        List<Long> topProductIds = dailyProductSalesService.findTopSellingProductIds(limit);
        return productService.findAllById(topProductIds);
    }
}