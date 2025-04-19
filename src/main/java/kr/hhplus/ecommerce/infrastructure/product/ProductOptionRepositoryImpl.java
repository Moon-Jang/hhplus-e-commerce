package kr.hhplus.ecommerce.infrastructure.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryImpl implements ProductOptionRepository {
    private final ProductOptionJpaRepository productOptionJpaRepository;

    @Override
    public Optional<ProductOption> findById(long id) {
        return productOptionJpaRepository.findById(id);
    }

    @Override
    public List<ProductOption> findAllByIds(List<Long> ids) {
        return productOptionJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<ProductOption> findAllByProductId(long productId) {
        return productOptionJpaRepository.findAllByProductId(productId);
    }

    @Override
    public List<ProductOption> findAllByProductIds(List<Long> productIds) {
        return productOptionJpaRepository.findAllByProductIdIn(productIds);
    }

    @Override
    public ProductOption save(ProductOption productOption) {
        return productOptionJpaRepository.save(productOption);
    }
} 