package kr.hhplus.ecommerce.application.product;

import kr.hhplus.ecommerce.common.constant.CacheNames;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final DailyProductSalesService dailyProductSalesService;

    @Cacheable(value = CacheNames.TOP_SELLING_PRODUCTS, key = "'limit-' + #limit")
    @Transactional(readOnly = true)
    public List<ProductVo> findTopSellingProducts(int limit) {
        List<Long> topProductIds = dailyProductSalesService.findTopSellingProductIds(limit);
        Map<Long, ProductVo> productMap = productService.findAllById(topProductIds)
            .stream()
            .collect(Collectors.toMap(ProductVo::id, Function.identity()));

        return topProductIds.stream()
            .map(productMap::get)
            .toList();
    }

    @CachePut(value = CacheNames.TOP_SELLING_PRODUCTS, key = "'limit-' + #limit")
    @Transactional(readOnly = true)
    public List<ProductVo> refreshTopSellingProducts(int limit) {
        List<Long> topProductIds = dailyProductSalesService.findTopSellingProductIds(limit);
        Map<Long, ProductVo> productMap = productService.findAllById(topProductIds)
            .stream()
            .collect(Collectors.toMap(ProductVo::id, Function.identity()));

        return topProductIds.stream()
            .map(productMap::get)
            .toList();
    }
}