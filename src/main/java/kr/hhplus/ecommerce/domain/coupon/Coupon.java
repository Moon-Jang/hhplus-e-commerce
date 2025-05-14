package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.common.DomainStatus;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Accessors(fluent = true)
public class Coupon {
    private final Long id;
    private String name;
    private int discountAmount;
    private LocalDateTime issueStartTime;
    private LocalDateTime issueEndTime;
    private int maxQuantity;
    private int issuedQuantity;
    private int expiryDays;

    public Coupon(Long id,
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

    public IssuedCoupon issue(long userId) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(this.issueStartTime) || now.isAfter(this.issueEndTime)) {
            throw new DomainException(DomainStatus.COUPON_ISSUANCE_NOT_AVAILABLE);
        }

        return new IssuedCoupon(
            userId,
            this.id,
            LocalDate.now().plusDays(this.expiryDays)
        );
    }
}