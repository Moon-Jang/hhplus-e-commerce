package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.common.Money;

public class PaymentCommand {
    public record Pay(
        long orderId
    ) {
    }

    public record SaveFailureHistory(
        long userId,
        Money amount,
        String failedReason
    ) {
    }
}