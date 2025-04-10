package kr.hhplus.ecommerce.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "payment_failure_histories")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PaymentFailureHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private Payment.Method payMethod;
    private int amount;
    private String reason;

    public PaymentFailureHistory(long userId,
                                 Payment.Method payMethod,
                                 int amount,
                                 String reason) {
        this.userId = userId;
        this.payMethod = payMethod;
        this.amount = amount;
        this.reason = reason;
    }
} 