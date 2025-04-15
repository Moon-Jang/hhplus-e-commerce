package kr.hhplus.ecommerce.infrastructure.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.hhplus.ecommerce.domain.product.ProductOption;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByIdIn(List<Long> ids);
    List<ProductOption> findAllByProductId(long productId);
    List<ProductOption> findAllByProductIdIn(List<Long> productIds);
} 