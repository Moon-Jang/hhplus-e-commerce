package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
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
    private int amount = 1000;
    private long refundAmount = 0;
    private Payment.Status status = Payment.Status.COMPLETED;
    private Payment.Method method = Payment.Method.POINT;

    @Override
    public Payment create() {
        Payment payment = new Payment();
        FixtureReflectionUtils.reflect(payment, this);
        return payment;
    }
}