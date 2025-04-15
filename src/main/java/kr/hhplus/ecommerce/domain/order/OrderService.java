package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    
    @Transactional
    public OrderVo create(OrderCommand.Create command) {
        List<ProductOption> productOptions = getProductOptions(command.items());
        Optional<IssuedCoupon> issuedCoupon = command.issuedCouponId()
            .flatMap(issuedCouponRepository::findById);
        Order order = createOrder(command, productOptions, issuedCoupon);

        return OrderVo.from(
            orderRepository.save(order)
        );
    }

    @Transactional
    public OrderVo complete(OrderCommand.Complete command) {
        Order order = orderRepository.findById(command.orderId())
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));

        order.complete();

        return OrderVo.from(
            orderRepository.save(order)
        );
    }

    private List<ProductOption> getProductOptions(List<OrderCommand.Create.OrderItem> items) {
        List<Long> optionIds = items.stream()
            .map(OrderCommand.Create.OrderItem::productOptionId)
            .toList();
        return productOptionRepository.findAllByIds(optionIds);
    }

    private Order createOrder(OrderCommand.Create command,
                              List<ProductOption> productOptions,
                              Optional<IssuedCoupon> issuedCoupon) {
        Map<Long, ProductOption> productOptionMap = productOptions.stream()
            .collect(Collectors.toMap(ProductOption::id, Function.identity()));

        return new Order(
            command.userId(),
            issuedCoupon.map(IssuedCoupon::id)
                .orElse(null),
            issuedCoupon.map(IssuedCoupon::coupon)
                .map(Coupon::discountAmount)
                .orElse(0),
            command.items()
                .stream()
                .map(item -> {
                    ProductOption productOption = productOptionMap.get(item.productOptionId());
                    return new OrderItem(
                        productOption.id(),
                        productOption.product().price(),
                        item.quantity()
                    );
                })
                .toList()
        );
    }
} 