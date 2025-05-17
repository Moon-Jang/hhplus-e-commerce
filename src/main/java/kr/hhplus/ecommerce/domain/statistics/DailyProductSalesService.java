package kr.hhplus.ecommerce.domain.statistics;

import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderItem;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionRepository;
import kr.hhplus.ecommerce.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DailyProductSalesService {
    static final int CHUNK_SIZE = 1000;
    private final DailyProductSalesRepository dailyProductSalesRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final OrderRepository orderRepository;

    public List<Long> findTopSellingProductIds(int limit) {
        LocalDate from = LocalDate.now().minusDays(3);
        LocalDate to = LocalDate.now();
        
        return dailyProductSalesRepository.findTopSellingProductIds(from, to, limit);
    }


    @Deprecated(since = "2025-05-13", forRemoval = true)
    @Transactional
    public void aggregate(LocalDate from, LocalDate to) {
        long totalProductCount = productRepository.count();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            dailyProductSalesRepository.deleteAllByAggregationDate(date);

            for (int i = 0; (long) i * CHUNK_SIZE < totalProductCount; i ++) {
                List<Long> productIds = productRepository.findAllIds(i, CHUNK_SIZE);
                var chunk = dailyProductSalesRepository.aggregate(date, productIds);
                dailyProductSalesRepository.createAll(chunk);
            }
        }
    }

    // 주문 완료 후 상품 판매량 증분을 반영
    @Transactional
    public void applySalesDelta(long orderId) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.status() == Order.Status.COMPLETED)
            .orElseThrow(() -> new BadRequestException(ORDER_NOT_FOUND));
        Map<Long, ProductOption> productOptionMap = productOptionRepository.findAllByIds(
                order.items().stream()
                    .map(OrderItem::productOptionId)
                    .toList()
            ).stream()
            .collect(Collectors.toMap(ProductOption::id, Function.identity()));

        order.items().forEach(item -> {
            DailyProductSales delta = DailyProductSales.createDelta(
                order.createdAt().toLocalDate(),
                productOptionMap.get(item.productOptionId()).product().id(),
                item.quantity()
            );

            dailyProductSalesRepository.saveDelta(delta);
        });
    }
}