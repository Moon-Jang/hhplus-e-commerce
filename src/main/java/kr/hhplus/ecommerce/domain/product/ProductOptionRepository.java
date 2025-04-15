package kr.hhplus.ecommerce.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository {
    Optional<ProductOption> findById(long id);
    List<ProductOption> findAllByIds(List<Long> ids);
    List<ProductOption> findAllByProductId(long productId);
    List<ProductOption> findAllByProductIds(List<Long> productIds);
    ProductOption save(ProductOption productOption);
} 