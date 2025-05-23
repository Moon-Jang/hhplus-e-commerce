package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.common.Money;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record OrderVo(
    long id,
    long userId,
    List<Item> items,
    Money totalAmount,
    Money discountAmount,
    Money finalAmount,
    Optional<Long> issuedCouponId,
    Order.Status status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static OrderVo from(Order order) {
        return new OrderVo(
            order.id(),
            order.userId(),
            order.items().stream()
                .map(Item::from)
                .toList(),
            order.priceDetails().totalAmount(),
            order.priceDetails().discountAmount(),
            order.priceDetails().finalAmount(),
            Optional.ofNullable(order.issuedCouponId()),
            order.status(),
            order.createdAt(),
            order.updatedAt()
        );
    }

    public record Item(
        long id,
        long productOptionId,
        int productPrice,
        int quantity,
        int amount
    ) {
        public static Item from(OrderItem orderItem) {
            return new Item(
                orderItem.id(),
                orderItem.productOptionId(),
                orderItem.productPrice(),
                orderItem.quantity(),
                orderItem.amount()
            );
        }
    }
}
