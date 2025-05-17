package kr.hhplus.ecommerce.infrastructure.coupon;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.infrastructure.common.PersistenceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "issued_coupons")
@Table(indexes = {
    @Index(name = "idx_issued_coupon_user_id", columnList = "userId"),
    @Index(name = "idx_issued_coupon_coupon_id", columnList = "coupon_id")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class IssuedCouponJpaEntity extends BaseEntity implements PersistenceEntity<IssuedCoupon> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private long couponId;
    private LocalDate expiryDate;
    private LocalDateTime usedAt;

    public IssuedCouponJpaEntity(Long id,
                                 long userId,
                                 long couponId,
                                 LocalDate expiryDate,
                                 LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.expiryDate = expiryDate;
        this.usedAt = usedAt;
    }

    public static IssuedCouponJpaEntity from(IssuedCoupon issuedCoupon) {
        return new IssuedCouponJpaEntity(
            issuedCoupon.id(),
            issuedCoupon.userId(),
            issuedCoupon.couponId(),
            issuedCoupon.expiryDate(),
            issuedCoupon.usedAt()
        );
    }

    @Override
    public IssuedCoupon toDomain() {
        return new IssuedCoupon(
            id,
            userId,
            couponId,
            expiryDate,
            usedAt
        );
    }
}
