package kr.hhplus.ecommerce.infrastructure.payment;

import kr.hhplus.ecommerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
} 