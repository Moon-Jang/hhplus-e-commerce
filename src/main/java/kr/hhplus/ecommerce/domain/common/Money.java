package kr.hhplus.ecommerce.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.function.Function;

public record Money(
    BigDecimal amount
) {
    public static final Money ZERO = Money.wons(0);

    public static Money wons(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static <T> Money sum(Collection<T> bags, Function<T, Money> monetary) {
        return bags.stream().map(monetary).reduce(Money.ZERO, Money::plus);
    }

    public Money plus(Money money) {
        return new Money(this.amount.add(money.amount));
    }

    public Money plus(BigDecimal amount) {
        return new Money(this.amount.add(amount));
    }

    public Money minus(Money money) {
        return new Money(this.amount.subtract(money.amount));
    }

    public Money minus(BigDecimal amount) {
        return new Money(this.amount.subtract(amount));
    }

    public Money times(double percent) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
    }

    public Money divide(double divisor) {
        return new Money(amount.divide(BigDecimal.valueOf(divisor), 10, RoundingMode.HALF_UP));
    }

    public boolean isLessThan(Money other) {
        return amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        return amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        return amount.compareTo(other.amount) <= 0;
    }

    public long longValue() {
        return amount.longValue();
    }

    public double doubleValue() {
        return amount.doubleValue();
    }

    public int intValue() {
        return amount.intValue();
    }

    public BigDecimal add(BigDecimal value) {
        return amount.add(value);
    }

    public BigDecimal subtract(BigDecimal value) {
        return amount.subtract(value);
    }

    public BigDecimal subtract(Money value) {
        return amount.subtract(value.amount);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Money other)) return false;
        return amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
