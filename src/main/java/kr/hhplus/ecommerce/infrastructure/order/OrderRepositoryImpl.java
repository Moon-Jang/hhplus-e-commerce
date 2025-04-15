package kr.hhplus.ecommerce.infrastructure.order;

import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    @Override
    public Optional<Order> findById(Long id) {
        // TODO: Implement me
        return Optional.empty();
    }

    @Override
    public Order save(Order order) {
        // TODO: Implement me
        return null;
    }
}
