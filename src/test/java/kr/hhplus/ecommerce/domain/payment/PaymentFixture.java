package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.domain.common.Money;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaymentFixture implements TestFixture<Payment> {
    private Long id = 1L;
    private long orderId = 1L;
    private long userId = 1L;
    private Money amount = Money.wons(1000);
    private long refundAmount = 0;
    private Payment.Status status = Payment.Status.COMPLETED;

    @Override
    public Payment create() {
        Payment payment = new Payment();
        FixtureReflectionUtils.reflect(payment, this);
        return payment;
    }
}