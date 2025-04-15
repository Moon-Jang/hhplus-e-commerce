package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.domain.common.Money;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderPriceFixture implements TestFixture<OrderPriceDetails> {
    private Money totalAmount = Money.wons(10000L);
    private Money discountAmount = Money.wons(1000L);
    private Money finalAmount = Money.wons(9000L);

    @Override
    public OrderPriceDetails create() {
        OrderPriceDetails price = new OrderPriceDetails();
        FixtureReflectionUtils.reflect(price, this);
        return price;
    }
}
