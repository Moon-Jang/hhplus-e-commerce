package kr.hhplus.ecommerce.infrastructure.payment;

import kr.hhplus.ecommerce.domain.payment.Payment;
import kr.hhplus.ecommerce.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    
    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
