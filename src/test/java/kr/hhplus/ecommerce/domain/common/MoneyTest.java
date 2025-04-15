package kr.hhplus.ecommerce.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {

    @Nested
    @DisplayName("정적 팩토리 메서드 테스트")
    class StaticFactoryMethodTest {
        
        @ParameterizedTest
        @ValueSource(longs = {0, 1, 100, 1000, 10000, 1000000})
        void wons_메서드는_정확한_금액을_가진_가격객체를_생성한다(long amount) {
            // when
            Money money = Money.wons(amount);
            
            // then
            assertThat(money.amount()).isEqualTo(BigDecimal.valueOf(amount));
            assertThat(money.longValue()).isEqualTo(amount);
        }
        
        @Test
        void ZERO_상수는_0원의_가격객체다() {
            // when & then
            assertThat(Money.ZERO.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(Money.ZERO.longValue()).isEqualTo(0L);
        }
        
        @ParameterizedTest
        @MethodSource("sumTestArguments")
        void sum_메서드는_컬렉션의_가격을_모두_더한다(List<Long> amounts, long expected) {
            // given
            List<TestItem> items = amounts.stream()
                .map(amount -> new TestItem(Money.wons(amount)))
                .toList();
            
            // when
            Money result = Money.sum(items, TestItem::price);
            
            // then
            assertThat(result.longValue()).isEqualTo(expected);
        }
        
        static Stream<Arguments> sumTestArguments() {
            return Stream.of(
                Arguments.of(Arrays.asList(0L, 0L, 0L), 0L),
                Arguments.of(Arrays.asList(100L, 200L, 300L), 600L),
                Arguments.of(Arrays.asList(1000L, 2000L, 3000L, 4000L), 10000L),
                Arguments.of(Arrays.asList(-100L, 200L, -50L), 50L)
            );
        }
    }
    
    @Nested
    @DisplayName("사칙연산 테스트")
    class ArithmeticOperationTest {
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "1000, 500, 1500",
            "10000, 5000, 15000",
            "-1000, 2000, 1000"
        })
        void plus_메서드는_두_가격을_더한다(long amount1, long amount2, long expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            Money result = money1.plus(money2);
            
            // then
            assertThat(result.longValue()).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "1000, 500, 500",
            "10000, 5000, 5000",
            "1000, 2000, -1000"
        })
        void minus_메서드는_가격을_뺀다(long amount1, long amount2, long expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            Money result = money1.minus(money2);
            
            // then
            assertThat(result.longValue()).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, 0",
            "1000, 0.5, 500",
            "10000, 0.1, 1000",
            "1000, 2, 2000"
        })
        void times_메서드는_가격에_비율을_곱한다(long amount, double multiplier, long expected) {
            // given
            Money money = Money.wons(amount);
            
            // when
            Money result = money.times(multiplier);
            
            // then
            assertThat(result.longValue()).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 1, 0",
            "1000, 2, 500",
            "10000, 10, 1000",
            "10, 3, 3.3333333333"
        })
        void divide_메서드는_가격을_나눈다(long amount, double divisor, String expected) {
            // given
            Money money = Money.wons(amount);
            BigDecimal expectedDecimal = new BigDecimal(expected);
            
            // when
            Money result = money.divide(divisor);
            
            // then
            assertThat(result.amount().setScale(10, RoundingMode.HALF_UP))
                .isEqualTo(expectedDecimal.setScale(10, RoundingMode.HALF_UP));
        }
    }
    
    @Nested
    @DisplayName("비교 연산 테스트")
    class ComparisonOperationTest {
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, false",
            "100, 200, true",
            "200, 100, false",
            "-100, 100, true"
        })
        void isLessThan_메서드는_작은지_비교한다(long amount1, long amount2, boolean expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            boolean result = money1.isLessThan(money2);
            
            // then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, false",
            "100, 200, false",
            "200, 100, true",
            "100, -100, true"
        })
        void isGreaterThan_메서드는_큰지_비교한다(long amount1, long amount2, boolean expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            boolean result = money1.isGreaterThan(money2);
            
            // then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, true",
            "100, 200, false",
            "200, 100, true",
            "100, 100, true"
        })
        void isGreaterThanOrEqual_메서드는_크거나_같은지_비교한다(long amount1, long amount2, boolean expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            boolean result = money1.isGreaterThanOrEqual(money2);
            
            // then
            assertThat(result).isEqualTo(expected);
        }
        
        @ParameterizedTest
        @CsvSource({
            "0, 0, true",
            "100, 200, true",
            "200, 100, false",
            "100, 100, true"
        })
        void isLessThanOrEqual_메서드는_작거나_같은지_비교한다(long amount1, long amount2, boolean expected) {
            // given
            Money money1 = Money.wons(amount1);
            Money money2 = Money.wons(amount2);
            
            // when
            boolean result = money1.isLessThanOrEqual(money2);
            
            // then
            assertThat(result).isEqualTo(expected);
        }
    }
    
    @Nested
    @DisplayName("값 접근 메서드 테스트")
    class ValueAccessMethodTest {
        
        @ParameterizedTest
        @ValueSource(longs = {0, 1000, 10000, -1000})
        void amount_메서드는_BigDecimal_값을_반환한다(long value) {
            // given
            Money money = Money.wons(value);
            
            // when
            BigDecimal result = money.amount();
            
            // then
            assertThat(result).isEqualTo(BigDecimal.valueOf(value));
        }
        
        @ParameterizedTest
        @ValueSource(longs = {0, 1000, 10000, -1000})
        void longValue_메서드는_long_값을_반환한다(long value) {
            // given
            Money money = Money.wons(value);
            
            // when
            long result = money.longValue();
            
            // then
            assertThat(result).isEqualTo(value);
        }
        
        @ParameterizedTest
        @ValueSource(longs = {0, 1000, 10000, -1000})
        void doubleValue_메서드는_double_값을_반환한다(long value) {
            // given
            Money money = Money.wons(value);
            
            // when
            double result = money.doubleValue();
            
            // then
            assertThat(result).isEqualTo((double) value);
        }
    }
    
    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {
        
        @ParameterizedTest
        @CsvSource({
            "0, 0.00",
            "1000, 1000.00",
            "1000.5, 1000.50",
            "-1000, -1000.00"
        })
        void toString_메서드는_금액을_문자열로_변환한다(BigDecimal value, String expected) {
            // given
            Money money = new Money(value);
            
            // when
            String result = money.toString();
            
            // then
            assertThat(result).isEqualTo(expected);
        }
    }
    
    // 테스트용 내부 클래스
    private static class TestItem {
        private final Money money;
        
        public TestItem(Money money) {
            this.money = money;
        }
        
        public Money price() {
            return money;
        }
    }
} 