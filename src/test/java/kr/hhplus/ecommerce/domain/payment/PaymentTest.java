package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.domain.common.Money;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PaymentTest {

    @Nested
    @DisplayName("생성 테스트")
    class ConstructorTest {
        @Test
        void 성공() {
            long orderId = 1L;
            long userId = 1L;
            Money amount = Money.wons(1000);
            // when
            Payment payment = new Payment(orderId, userId, amount);

            // then
            Assertions.assertThat(payment.orderId()).isEqualTo(orderId);
            Assertions.assertThat(payment.userId()).isEqualTo(userId);
            Assertions.assertThat(payment.amount()).isEqualTo(amount);
            Assertions.assertThat(payment.refundAmount()).isEqualTo(0);
            Assertions.assertThat(payment.status()).isEqualTo(Payment.Status.COMPLETED);
        }
    }
}