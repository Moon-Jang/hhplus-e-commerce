package kr.hhplus.ecommerce.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "payments")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long orderId;
    private long userId;
    private int amount;
    private long refundAmount;
    @Enumerated(EnumType.STRING)
    private Payment.Status status;
    @Enumerated(EnumType.STRING)
    private Payment.Method method;

    public Payment(long orderId,
                   long userId,
                   int amount,
                   Method method) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.method = method;
        this.refundAmount = 0;
        this.status = Status.COMPLETED;
    }

    public enum Status {
        COMPLETED, // 결제 완료
        REFUNDED, // 환불
    }

    public enum Method {
        POINT, // 포인트 결제
        CARD, // 카드 결제
    }
} 