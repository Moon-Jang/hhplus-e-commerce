package kr.hhplus.ecommerce.infrastructure.order;

import kr.hhplus.ecommerce.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(long userId);
} 