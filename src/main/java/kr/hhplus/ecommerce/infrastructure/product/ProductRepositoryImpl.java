package kr.hhplus.ecommerce.infrastructure.product;

import kr.hhplus.ecommerce.domain.product.Product;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Optional<Product> findById(long id) {
        // TODO: Implement me
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        // TODO: Implement me
        return Collections.emptyList();
    }

    @Override
    public List<Product> findAllById(List<Long> ids) {
        // TODO: Implement me
        return Collections.emptyList();
    }
} 