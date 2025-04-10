package kr.hhplus.ecommerce.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record OrderVo(
    long id,
    long userId,
    List<Item> items,
    int totalAmount,
    int discountAmount,
    int finalAmount,
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
            order.totalAmount(),
            order.discountAmount(),
            order.finalAmount(),
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
