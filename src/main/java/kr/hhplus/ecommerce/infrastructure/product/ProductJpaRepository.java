package kr.hhplus.ecommerce.infrastructure.product;

import kr.hhplus.ecommerce.domain.product.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByIdIn(List<Long> ids);
    @Query("SELECT p.id FROM products p")
    List<Long> findAllIds(Pageable pageable);
} 