package kr.hhplus.ecommerce.domain.order;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.domain.common.Money;
import kr.hhplus.ecommerce.domain.common.MoneyConverter;
import lombok.Getter;
import lombok.experimental.Accessors;

import static kr.hhplus.ecommerce.common.support.DomainStatus.INVALID_ORDER_PRICE;

@Embeddable
@Getter
@Accessors(fluent = true)
public class OrderPriceDetails {
    @Convert(converter = MoneyConverter.class)
    private Money totalAmount;
    
    @Convert(converter = MoneyConverter.class)
    private Money discountAmount;
    
    @Convert(converter = MoneyConverter.class)
    private Money finalAmount;

    public OrderPriceDetails() {
        this.totalAmount = Money.ZERO;
        this.discountAmount = Money.ZERO;
        this.finalAmount = Money.ZERO;
    }

    public void addAmount(int amount) {
        Money amountToAdd = Money.wons(amount);
        this.totalAmount = this.totalAmount.plus(amountToAdd);
        this.finalAmount = this.finalAmount.plus(amountToAdd);
        validateValues();
    }

    public void addDiscount(int discount) {
        Money discountToAdd = Money.wons(discount);
        this.discountAmount = this.discountAmount.plus(discountToAdd);
        this.finalAmount = this.finalAmount.minus(discountToAdd);
        validateValues();
    }

    void validateValues() {
        if (totalAmount.isLessThan(Money.ZERO)) {
            throw new DomainException(INVALID_ORDER_PRICE, "총 금액은 0원 이상이어야 합니다.");
        }

        if (discountAmount.isLessThan(Money.ZERO)) {
            throw new DomainException(INVALID_ORDER_PRICE, "할인 금액은 0원 이상이어야 합니다.");
        }

        if (finalAmount.isLessThan(Money.ZERO)) {
            throw new DomainException(INVALID_ORDER_PRICE, "최종 금액은 0원 이상이어야 합니다.");
        }
    }
}
