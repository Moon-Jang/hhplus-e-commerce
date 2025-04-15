package kr.hhplus.ecommerce.infrastructure.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.hhplus.ecommerce.domain.product.Product;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByIdIn(List<Long> ids);
} 