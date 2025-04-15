package kr.hhplus.ecommerce.domain.payment;

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
            int amount = 1000;
            Payment.Method method = Payment.Method.POINT;

            // when
            Payment payment = new Payment(orderId, userId, amount, method);

            // then
            Assertions.assertThat(payment.orderId()).isEqualTo(orderId);
            Assertions.assertThat(payment.userId()).isEqualTo(userId);
            Assertions.assertThat(payment.amount()).isEqualTo(amount);
            Assertions.assertThat(payment.method()).isEqualTo(method);
            Assertions.assertThat(payment.refundAmount()).isEqualTo(0);
            Assertions.assertThat(payment.status()).isEqualTo(Payment.Status.COMPLETED);
        }
    }
}