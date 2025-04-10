package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ALREADY_COMPLETED_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OrderTest {

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateTest {
        @Test
        void 주문_생성_성공() {
            // given
            long userId = 1L;
            Long issuedCouponId = 2L;
            int discountAmount = 1000;
            List<OrderItem> items = List.of(
                new OrderItem(1L, 5000, 2),
                new OrderItem(2L, 5000, 1)
            );
            int expectedTotalAmount = 15000;
            int expectedFinalAmount = expectedTotalAmount - discountAmount;

            // when
            Order order = new Order(
                userId,
                issuedCouponId,
                discountAmount,
                items
            );
            
            // then
            assertThat(order.userId()).isEqualTo(userId);
            assertThat(order.issuedCouponId()).isEqualTo(issuedCouponId);
            assertThat(order.totalAmount()).isEqualTo(expectedTotalAmount);
            assertThat(order.discountAmount()).isEqualTo(discountAmount);
            assertThat(order.finalAmount()).isEqualTo(expectedFinalAmount);
            assertThat(order.status()).isEqualTo(Order.Status.PENDING);
            assertThat(order.items()).isEqualTo(items);
        }
    }

    @Nested
    @DisplayName("주문 완료 테스트")
    class CompleteTest {
        @Test
        void 완료_성공() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .create();
            
            // when
            order.complete();
            
            // then
            assertThat(order.status()).isEqualTo(Order.Status.COMPLETED);
        }
        
        @ParameterizedTest
        @EnumSource(value = Order.Status.class, names = {"COMPLETED", "REFUNDED"})
        void 이미_완료된_주문_상태일_때_실패(Order.Status status) {
            // given
            Order order = new OrderFixture()
                .setStatus(status)
                .create();
            
            // when
            Throwable throwable = catchThrowable(order::complete);
            
            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", ALREADY_COMPLETED_ORDER)
                .hasMessage(ALREADY_COMPLETED_ORDER.message());
        }
    }
}