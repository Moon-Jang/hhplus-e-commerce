package kr.hhplus.ecommerce.infrastructure.product;

import kr.hhplus.ecommerce.domain.product.Product;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public List<Product> findAllById(List<Long> ids) {
        return productJpaRepository.findAllByIdIn(ids);
    }

    @Override
    public List<Long> findAllIds(int page, int size) {
        return productJpaRepository.findAllIds(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
    }

    @Override
    public long count() {
        return productJpaRepository.count();
    }
} 