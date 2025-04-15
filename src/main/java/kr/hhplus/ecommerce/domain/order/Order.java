package kr.hhplus.ecommerce.domain.order;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import kr.hhplus.ecommerce.common.exception.DomainException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ALREADY_COMPLETED_ORDER;

@Entity(name = "orders")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private Long issuedCouponId;
    private int totalAmount;
    private int discountAmount;
    private int finalAmount;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items = new ArrayList<>();
    
    public Order(long userId,
                 Long issuedCouponId,
                 int discountAmount,
                 List<OrderItem> items) {
        this.userId = userId;
        this.issuedCouponId = issuedCouponId;
        this.totalAmount = items.stream()
            .mapToInt(OrderItem::amount)
            .sum();
        this.discountAmount = discountAmount;
        this.finalAmount = this.totalAmount - this.discountAmount;
        this.status = Status.PENDING;
        this.items = items;
    }
    
    public void complete() {
        if (status != Status.PENDING) {
            throw new DomainException(ALREADY_COMPLETED_ORDER);
        }

        this.status = Status.COMPLETED;
    }

    public enum Status {
        PENDING,   // 주문 생성 후 결제 전
        COMPLETED, // 결제 완료
        REFUNDED,  // 환불 완료
    }
} 