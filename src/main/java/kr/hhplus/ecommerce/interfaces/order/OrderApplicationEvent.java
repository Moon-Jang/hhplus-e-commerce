package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.payment.Payment;

public class OrderApplicationEvent {
    public record PaymentFailure(
        OrderVo order,
        Payment.Method payMethod,
        String failedReason
    ) {
    }

    public record Complete(
        OrderVo order
    ) {
    }
}
