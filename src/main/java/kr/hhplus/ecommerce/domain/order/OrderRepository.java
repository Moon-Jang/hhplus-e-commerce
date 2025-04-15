package kr.hhplus.ecommerce.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    Order save(Order order);
    List<Long> findTopSellingProductIds(int limit);
} 