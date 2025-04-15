package kr.hhplus.ecommerce.infrastructure.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.domain.order.QOrder.order;
import static kr.hhplus.ecommerce.domain.order.QOrderItem.orderItem;
import static kr.hhplus.ecommerce.domain.product.QProductOption.productOption;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JPAQueryFactory queryFactory;
    
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
        return queryFactory
            .select(productOption.product.id)
            .from(order)
            .join(order.items, orderItem)
            .join(productOption).on(orderItem.productOptionId.eq(productOption.id))
            .where(order.status.eq(Order.Status.COMPLETED))
            .groupBy(productOption.product.id)
            .orderBy(orderItem.count().desc())
            .limit(limit)
            .fetch();
    }
}
