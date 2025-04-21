package kr.hhplus.ecommerce.infrastructure.product;

import kr.hhplus.ecommerce.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findAllByIdIn(List<Long> ids);
    List<ProductOption> findAllByProductId(long productId);
    List<ProductOption> findAllByProductIdIn(List<Long> productIds);
} 