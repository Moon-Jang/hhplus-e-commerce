package kr.hhplus.ecommerce.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(long id);
    List<Product> findAll();
    List<Product> findAllById(List<Long> ids);
} 