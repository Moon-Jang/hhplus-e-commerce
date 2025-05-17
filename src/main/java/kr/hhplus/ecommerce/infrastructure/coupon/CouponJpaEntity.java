package kr.hhplus.ecommerce.infrastructure.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.coupon.Coupon;
import kr.hhplus.ecommerce.infrastructure.common.PersistenceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity(name = "coupons")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CouponJpaEntity extends BaseEntity implements PersistenceEntity<Coupon> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int discountAmount;
    private LocalDateTime issueStartTime;
    private LocalDateTime issueEndTime;
    private int maxQuantity;
    private int issuedQuantity;
    private int expiryDays;

    public CouponJpaEntity(Long id,
                           String name,
                           int discountAmount,
                           LocalDateTime issueStartTime,
                           LocalDateTime issueEndTime,
                           int maxQuantity,
                           int issuedQuantity,
                           int expiryDays) {
        this.id = id;
        this.name = name;
        this.discountAmount = discountAmount;
        this.issueStartTime = issueStartTime;
        this.issueEndTime = issueEndTime;
        this.maxQuantity = maxQuantity;
        this.issuedQuantity = issuedQuantity;
        this.expiryDays = expiryDays;
    }

    public static CouponJpaEntity from(Coupon coupon) {
        return new CouponJpaEntity(
            coupon.id(),
            coupon.name(),
            coupon.discountAmount(),
            coupon.issueStartTime(),
            coupon.issueEndTime(),
            coupon.maxQuantity(),
            coupon.issuedQuantity(),
            coupon.expiryDays()
        );
    }

    @Override
    public Coupon toDomain() {
        return new Coupon(
            id,
            name,
            discountAmount,
            issueStartTime,
            issueEndTime,
            maxQuantity,
            issuedQuantity,
            expiryDays
        );
    }
}
