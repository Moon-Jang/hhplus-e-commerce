package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.common.Money;

import java.time.LocalDateTime;

public record PaymentVo(
    long id,
    long orderId,
    long userId,
    Money amount,
    Payment.Status status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PaymentVo from(Payment payment) {
        return new PaymentVo(
            payment.id(),
            payment.orderId(),
            payment.userId(),
            payment.amount(),
            payment.status(),
            payment.createdAt(),
            payment.updatedAt()
        );
    }
}