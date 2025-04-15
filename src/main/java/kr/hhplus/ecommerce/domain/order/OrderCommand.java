package kr.hhplus.ecommerce.domain.order;

import java.util.List;
import java.util.Optional;

public class OrderCommand {

    public record Create(
        long userId,
        List<OrderItem> items,
        Optional<Long> issuedCouponId
    ) {
        public record OrderItem(
            long productOptionId,
            int quantity
        ) {
        }
    }

    public record Complete(
        long orderId
    ) {
    }
} 