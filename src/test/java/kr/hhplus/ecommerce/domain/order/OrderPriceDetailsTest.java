package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.domain.common.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.ecommerce.common.support.DomainStatus.INVALID_ORDER_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OrderPriceDetailsTest {

    @Test
    @DisplayName("OrderPrice 생성 테스트")
    void 생성자_테스트() {
        // when
        OrderPriceDetails orderPriceDetails = new OrderPriceDetails();

        // then
        assertThat(orderPriceDetails.totalAmount()).isEqualTo(Money.ZERO);
        assertThat(orderPriceDetails.discountAmount()).isEqualTo(Money.ZERO);
        assertThat(orderPriceDetails.finalAmount()).isEqualTo(Money.ZERO);
    }

    @Nested
    @DisplayName("금액 추가 테스트")
    class AddAmountTest {
        @Test
        void 금액_추가_성공() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceDetails();
            int amount = 10000;

            // when
            orderPriceDetails.addAmount(amount);

            // then
            assertThat(orderPriceDetails.totalAmount()).isEqualTo(Money.wons(amount));
            assertThat(orderPriceDetails.finalAmount()).isEqualTo(Money.wons(amount));
            assertThat(orderPriceDetails.discountAmount()).isEqualTo(Money.ZERO);
        }

        @Test
        void 여러번_금액_추가_성공() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceDetails();

            // when
            orderPriceDetails.addAmount(10000);
            orderPriceDetails.addAmount(5000);

            // then
            assertThat(orderPriceDetails.totalAmount()).isEqualTo(Money.wons(15000));
            assertThat(orderPriceDetails.finalAmount()).isEqualTo(Money.wons(15000));
            assertThat(orderPriceDetails.discountAmount()).isEqualTo(Money.ZERO);
        }
    }

    @Nested
    @DisplayName("할인 추가 테스트")
    class AddDiscountTest {
        @Test
        void 할인_추가_성공() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceFixture()
                .setTotalAmount(Money.wons(10000))
                .setDiscountAmount(Money.ZERO)
                .setFinalAmount(Money.wons(10000))
                .create();
            int discount = 1000;

            // when
            orderPriceDetails.addDiscount(discount);

            // then
            assertThat(orderPriceDetails.totalAmount()).isEqualTo(Money.wons(10000));
            assertThat(orderPriceDetails.discountAmount()).isEqualTo(Money.wons(discount));
            assertThat(orderPriceDetails.finalAmount()).isEqualTo(Money.wons(9000));
        }

        @Test
        void 여러번_할인_추가_성공() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceFixture()
                .setTotalAmount(Money.wons(10000))
                .setDiscountAmount(Money.ZERO)
                .setFinalAmount(Money.wons(10000))
                .create();

            // when
            orderPriceDetails.addDiscount(1000);
            orderPriceDetails.addDiscount(500);

            // then
            assertThat(orderPriceDetails.totalAmount()).isEqualTo(Money.wons(10000));
            assertThat(orderPriceDetails.discountAmount()).isEqualTo(Money.wons(1500));
            assertThat(orderPriceDetails.finalAmount()).isEqualTo(Money.wons(8500));
        }

        @Test
        void 전체_금액보다_큰_할인_추가시_실패() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceFixture()
                .setTotalAmount(Money.wons(10000))
                .setDiscountAmount(Money.ZERO)
                .setFinalAmount(Money.wons(10000))
                .create();
            int discount = 11000;

            // when
            Throwable throwable = catchThrowable(() -> orderPriceDetails.addDiscount(discount));

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_ORDER_PRICE)
                .hasMessage("최종 금액은 0원 이상이어야 합니다.");
        }
    }

    @Nested
    @DisplayName("금액 검증 테스트")
    class ValidateValuesTest {
        @Test
        void total_amount가__0보다_작으면_실패() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceDetails();
            FixtureReflectionUtils.setField(orderPriceDetails, "totalAmount", Money.wons(-1));

            // when
            Throwable throwable = catchThrowable(orderPriceDetails::validateValues);

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_ORDER_PRICE)
                .hasMessage("총 금액은 0원 이상이어야 합니다.");
        }

        @Test
        void discount_amount가_0보다_작으면_실패() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceDetails();
            FixtureReflectionUtils.setField(orderPriceDetails, "discountAmount", Money.wons(-1));

            // when
            Throwable throwable = catchThrowable(orderPriceDetails::validateValues);

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_ORDER_PRICE)
                .hasMessage("할인 금액은 0원 이상이어야 합니다.");
        }

        @Test
        void final_amount가_0보다_작으면_실패() {
            // given
            OrderPriceDetails orderPriceDetails = new OrderPriceDetails();
            FixtureReflectionUtils.setField(orderPriceDetails, "finalAmount", Money.wons(-1));

            // when
            Throwable throwable = catchThrowable(orderPriceDetails::validateValues);

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_ORDER_PRICE)
                .hasMessage("최종 금액은 0원 이상이어야 합니다.");
        }
    }
} 