package kr.hhplus.ecommerce.domain.payment;

public class PaymentCommand {
    public record Pay(
        long orderId,
        Payment.Method payMethod
    ) {
    }

    public record SaveFailureHistory(
        long userId,
        int amount,
        Payment.Method payMethod,
        String failedReason
    ) {
    }
}