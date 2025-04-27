package kr.hhplus.ecommerce.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.common.support.DomainStatus;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "coupons")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
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

    public IssuedCoupon issue(long userId) {
        if (this.issuedQuantity >= this.maxQuantity) {
            throw new DomainException(DomainStatus.COUPON_EXHAUSTED);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.issueStartTime) || now.isAfter(this.issueEndTime)) {
            throw new DomainException(DomainStatus.COUPON_ISSUANCE_NOT_AVAILABLE);
        }

        this.issuedQuantity++;

        return new IssuedCoupon(
            userId,
            LocalDate.now().plusDays(this.expiryDays),
            this
        );
    }
}