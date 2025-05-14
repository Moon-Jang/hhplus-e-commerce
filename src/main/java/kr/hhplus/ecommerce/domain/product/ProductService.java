package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.constant.CacheNames;
import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.common.CacheTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.PRODUCT_NOT_FOUND;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.PRODUCT_OPTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CacheTemplate cacheTemplate;

    @Transactional(readOnly = true)
    public List<ProductVo> findAll() {
        List<Product> products = productRepository.findAll()
            .stream()
            .toList();
        List<Long> productIds = products.stream()
            .map(Product::id)
            .toList();

        Map<Long, List<ProductOption>> productOptionListMap = productOptionRepository
            .findAllByProductIds(productIds)
            .stream()
            .collect(Collectors.groupingBy(option -> option.product().id()));

        return products.stream()
            .map(product -> ProductVo.from(
                product,
                productOptionListMap.get(product.id())
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductVo> findAllById(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds)
            .stream()
            .toList();

        Map<Long, List<ProductOption>> productOptionListMap = productOptionRepository
            .findAllByProductIds(productIds)
            .stream()
            .collect(Collectors.groupingBy(option -> option.product().id()));

        return products.stream()
            .map(product -> ProductVo.from(
                product,
                productOptionListMap.get(product.id())
            ))
            .toList();
    }

    @Cacheable(value = CacheNames.PRODUCT_DETAILS, key = "#productId")
    @Transactional(readOnly = true)
    public ProductVo findById(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
        List<ProductOption> productOptions = productOptionRepository.findAllByProductId(productId);
        return ProductVo.from(product, productOptions);
    }

    @Transactional
    public void deductStock(ProductCommand.DeductStock command) {
        command.items()
            .forEach(item -> {
                ProductOption option = productOptionRepository.findByIdWithLock(item.productOptionId())
                    .orElseThrow(() -> new BadRequestException(PRODUCT_OPTION_NOT_FOUND));
                option.deductStock(item.quantity());
                productOptionRepository.save(option);
                cacheTemplate.evictAsync(CacheNames.PRODUCT_DETAILS + "::" + option.product().id());
            });
    }
}