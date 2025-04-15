package kr.hhplus.ecommerce.infrastructure.order;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderRepository;

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
    
    @Override
    public List<Long> findTopSellingProductIds(int limit) {
        // TODO: 실제 구현시 DB 쿼리로 변경 필요
        // 임시 구현: 인기있는 상품 ID 5개를 반환
        return Arrays.asList(1L, 2L, 3L, 4L, 5L).subList(0, Math.min(limit, 5));
    }
}
