package kr.hhplus.ecommerce.infrastructure.payment;

import kr.hhplus.ecommerce.domain.payment.Payment;
import kr.hhplus.ecommerce.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    @Override
    public Payment save(Payment payment) {
        // TODO: Implement me
        return null;
    }
}
