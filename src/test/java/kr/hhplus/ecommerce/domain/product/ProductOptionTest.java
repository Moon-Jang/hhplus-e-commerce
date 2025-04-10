package kr.hhplus.ecommerce.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import kr.hhplus.ecommerce.common.exception.DomainException;
import static kr.hhplus.ecommerce.common.support.DomainStatus.INSUFFICIENT_STOCK;
import static kr.hhplus.ecommerce.common.support.DomainStatus.INVALID_PARAMETER;

class ProductOptionTest {

    @Nested
    @DisplayName("재고 차감 테스트")
    class DecreaseStockTest {
        @ParameterizedTest
        @ValueSource(ints = { 0, -1, -10, -100 })
        void 요청_수량이_0_이하인_경우_실패(int quantity) {
            // given
            ProductOption option = new ProductOptionFixture()
                .setStock(1000)
                .create();

            // when
            Throwable throwable = catchThrowable(() -> option.decreaseStock(quantity));

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_PARAMETER)
                .hasMessageContaining("차감될 수량은 0이하 일 수 없습니다.");
        }

        @ParameterizedTest
        @MethodSource("insufficientStockArguments")
        void 재고보다_많은_수량_차감_시_실패(int initialQuantity, int decreaseQuantity) {
            // given
            ProductOption option = new ProductOptionFixture()
                .setStock(initialQuantity)
                .create();

            // when
            Throwable throwable = catchThrowable(() -> option.decreaseStock(decreaseQuantity));

            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INSUFFICIENT_STOCK)
                .hasMessageContaining(INSUFFICIENT_STOCK.message());
        }

        static Object[][] insufficientStockArguments() {
            return new Object[][] {
                { 0, 1 },
                { 10, 20 },
                { 100, 101 },
                { 1000, 1001 }
            };
        }

        @ParameterizedTest
        @MethodSource("successArguments")
        void 유효한_수량으로_재고_차감_시_성공(int initialQuantity, int decreaseQuantity) {
            // given
            ProductOption option = new ProductOptionFixture()
                .setStock(initialQuantity)
                .create();
            int expectedQuantity = initialQuantity - decreaseQuantity;

            // when
            option.decreaseStock(decreaseQuantity);

            // then
            assertThat(option.stock()).isEqualTo(expectedQuantity);
        }

        static Object[][] successArguments() {
            return new Object[][] {
                { 10, 1 },
                { 10, 9 },
                { 100, 100 },
                { 1000, 500 }
            };
        }
    }
}