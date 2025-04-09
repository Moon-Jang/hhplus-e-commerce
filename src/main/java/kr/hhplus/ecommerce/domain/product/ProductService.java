package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.common.support.DomainStatus.PRODUCT_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.PRODUCT_OPTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

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

    @Transactional(readOnly = true)
    public ProductVo findById(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
        List<ProductOption> productOptions = productOptionRepository.findAllByProductId(productId);
        return ProductVo.from(product, productOptions);
    }

    @Transactional
    public void decreaseStock(long optionId, int quantity) {
        ProductOption option = productOptionRepository.findById(optionId)
            .orElseThrow(() -> new NotFoundException(PRODUCT_OPTION_NOT_FOUND));

        option.decreaseStock(quantity);
        productOptionRepository.save(option);
    }
}