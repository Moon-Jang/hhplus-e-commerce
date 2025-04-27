package kr.hhplus.ecommerce.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.common.Money;
import kr.hhplus.ecommerce.domain.common.MoneyConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "payments")
@Table(indexes = {
    @Index(name = "idx_payment_order_id", columnList = "orderId"),
    @Index(name = "idx_payment_user_id", columnList = "userId")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long orderId;
    private long userId;
    @Convert(converter = MoneyConverter.class)
    private Money amount;
    
    private long refundAmount;
    @Enumerated(EnumType.STRING)
    private Payment.Status status;

    public Payment(long orderId,
                   long userId,
                   Money amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.refundAmount = 0;
        this.status = Status.COMPLETED;
    }

    public enum Status {
        COMPLETED, // 결제 완료
        REFUNDED, // 환불
    }
} 