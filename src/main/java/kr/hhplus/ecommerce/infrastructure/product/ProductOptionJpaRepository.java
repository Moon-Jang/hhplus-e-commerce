package kr.hhplus.ecommerce.infrastructure.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM product_options po WHERE po.id = :id")
    Optional<ProductOption> findByIdWithLock(@Param("id") long id);

    List<ProductOption> findAllByIdIn(List<Long> ids);
    List<ProductOption> findAllByProductId(long productId);
    List<ProductOption> findAllByProductIdIn(List<Long> productIds);
} 