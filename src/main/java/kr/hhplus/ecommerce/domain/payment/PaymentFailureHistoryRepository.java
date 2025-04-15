package kr.hhplus.ecommerce.domain.payment;

public interface PaymentFailureHistoryRepository {
    PaymentFailureHistory save(PaymentFailureHistory history);
} 