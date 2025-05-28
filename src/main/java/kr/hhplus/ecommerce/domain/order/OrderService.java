package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.domain.coupon.CouponRepository;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponRepository couponRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderVo create(OrderCommand.Create command) {
        Order order = new Order(command.userId());

        Map<Long, ProductOption> productOptionMap = getProductOptionMap(command.items());
        command.items().forEach(item -> {
            ProductOption productOption = productOptionMap.get(item.productOptionId());
            order.addItem(productOption, item.quantity());
        });

        command.issuedCouponId()
            .flatMap(issuedCouponRepository::findById)
            .ifPresent(issuedCoupon -> {
                Coupon coupon = couponRepository.findById(issuedCoupon.couponId())
                    .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
                order.applyCoupon(issuedCoupon, coupon);
            });

        return OrderVo.from(
            orderRepository.save(order)
        );
    }

    @Transactional
    public OrderVo complete(OrderCommand.Complete command) {
        Order order = orderRepository.findById(command.orderId())
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));

        order.complete();
        eventPublisher.publishEvent(new OrderEvent.Completed(OrderVo.from(order)));

        return OrderVo.from(
            orderRepository.save(order)
        );
    }

    @Transactional(readOnly = true)
    public List<Long> findTopSellingProductIds(int limit) {
        return orderRepository.findTopSellingProductIds(limit);
    }

    private Map<Long, ProductOption> getProductOptionMap(List<OrderCommand.Create.OrderItem> items) {
        List<Long> optionIds = items.stream()
            .map(OrderCommand.Create.OrderItem::productOptionId)
            .toList();

        return productOptionRepository.findAllByIds(optionIds)
            .stream()
            .collect(Collectors.toMap(ProductOption::id, Function.identity()));
    }
} 