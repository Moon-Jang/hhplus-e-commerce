package kr.hhplus.ecommerce.infrastructure.product;

import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryImpl implements ProductOptionRepository {

    @Override
    public Optional<ProductOption> findById(long id) {
        // TODO: Implement me
        return Optional.empty();
    }

    @Override
    public List<ProductOption> findAllByIds(List<Long> ids) {
        // TODO: Implement me
        return Collections.emptyList();
    }

    @Override
    public List<ProductOption> findAllByProductId(long productId) {
        // TODO: Implement me
        return List.of();
    }

    @Override
    public List<ProductOption> findAllByProductIds(List<Long> productIds) {
        // TODO: Implement me
        return List.of();
    }

    @Override
    public ProductOption save(ProductOption productOption) {
        // TODO: Implement me
        return null;
    }
} 