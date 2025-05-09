package kr.hhplus.ecommerce.application.product;

import kr.hhplus.ecommerce.config.CacheNames;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final DailyProductSalesService dailyProductSalesService;

    @Cacheable(value = CacheNames.TOP_SELLING_PRODUCTS, key = "'limit-' + #limit")
    @Transactional(readOnly = true)
    public List<ProductVo> findTopSellingProducts(int limit) {
        List<Long> topProductIds = dailyProductSalesService.findTopSellingProductIds(limit);
        return productService.findAllById(topProductIds);
    }

    @CachePut(value = CacheNames.TOP_SELLING_PRODUCTS, key = "'limit-' + #limit")
    @Transactional(readOnly = true)
    public List<ProductVo> refreshTopSellingProducts(int limit) {
        List<Long> topProductIds = dailyProductSalesService.findTopSellingProductIds(limit);
        return productService.findAllById(topProductIds);
    }
}