package kr.hhplus.ecommerce.infrastructure.payment;

import kr.hhplus.ecommerce.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
} 