package kr.hhplus.ecommerce.domain.order;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.ALREADY_COMPLETED_ORDER;

@Entity(name = "orders")
@Table(indexes = {
    @Index(name = "idx_order_user_id", columnList = "userId"),
    @Index(name = "idx_order_issued_coupon_id", columnList = "issuedCouponId")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private Long issuedCouponId;
    @Embedded
    private OrderPriceDetails priceDetails;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items = new ArrayList<>();
    
    public Order(long userId) {
        this.userId = userId;
        this.issuedCouponId = null;
        this.priceDetails = new OrderPriceDetails();
        this.status = Status.PENDING;
        this.items = new ArrayList<>();
    }
    
    public void complete() {
        if (status != Status.PENDING) {
            throw new DomainException(ALREADY_COMPLETED_ORDER);
        }

        this.status = Status.COMPLETED;
    }

    public void applyCoupon(IssuedCoupon issuedCoupon, Coupon coupon) {
        issuedCoupon.validateUsable();
        this.issuedCouponId = issuedCoupon.id();
        this.priceDetails.addDiscount(coupon.discountAmount());
    }

    public void addItem(ProductOption productOption, int quantity) {
        OrderItem newItem = new OrderItem(productOption, quantity);
        this.items.add(newItem);
        this.priceDetails.addAmount(newItem.amount());
    }

    public enum Status {
        PENDING,   // 주문 생성 후 결제 전
        COMPLETED, // 결제 완료
        REFUNDED,  // 환불 완료
    }
} 