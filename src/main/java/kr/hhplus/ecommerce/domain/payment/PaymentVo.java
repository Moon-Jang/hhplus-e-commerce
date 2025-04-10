package kr.hhplus.ecommerce.domain.payment;

import java.time.LocalDateTime;

public record PaymentVo(
    long id,
    long orderId,
    long userId,
    Payment.Method method,
    Payment.Status status,
    long amount,
    LocalDateTime createdAt
) {
    public static PaymentVo from(Payment payment) {
        return new PaymentVo(
            payment.id(),
            payment.orderId(),
            payment.userId(),
            payment.method(),
            payment.status(),
            payment.amount(),
            payment.createdAt()
        );
    }
}