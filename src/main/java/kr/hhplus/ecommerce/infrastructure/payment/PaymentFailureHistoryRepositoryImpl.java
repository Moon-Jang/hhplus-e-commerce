package kr.hhplus.ecommerce.infrastructure.payment;

import kr.hhplus.ecommerce.domain.payment.PaymentFailureHistory;
import kr.hhplus.ecommerce.domain.payment.PaymentFailureHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentFailureHistoryRepositoryImpl implements PaymentFailureHistoryRepository {
    @Override
    public PaymentFailureHistory save(PaymentFailureHistory history) {
        // TODO: Implement me
        return null;
    }
}