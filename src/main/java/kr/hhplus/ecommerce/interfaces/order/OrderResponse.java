package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderVo;

public class OrderResponse {
    public record OrderResult(
        long id,
        Order.Status status
    ) {
        public static OrderResult from(OrderVo order) {
            return new OrderResult(
                order.id(),
                order.status()
            );
        }
    }
}
