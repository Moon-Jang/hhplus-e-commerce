package kr.hhplus.ecommerce.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import kr.hhplus.ecommerce.common.exception.DomainException;
import static kr.hhplus.ecommerce.common.support.DomainStatus.EXCEEDED_MAX_USER_POINT;
import static kr.hhplus.ecommerce.common.support.DomainStatus.INVALID_CHARGE_AMOUNT;

public class UserPointTest {
    
    @Nested
    @DisplayName("충전 테스트")
    class ChargeTest {
        @ParameterizedTest
        @ValueSource(ints = {UserPoint.MIN_CHARGE_AMOUNT - 1, UserPoint.MAX_CHARGE_AMOUNT + 1})
        void 요청한_충전_금액이_지정한_숫자_범위를_벗어날_경우_실패(int amount) {
            // given
            UserPoint userPoint = new UserPointFixture().create();
            
            // when
            Throwable throwable = catchThrowable(() -> userPoint.charge(amount));
            
            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                    .hasFieldOrPropertyWithValue("status", INVALID_CHARGE_AMOUNT)
                    .hasMessage(INVALID_CHARGE_AMOUNT.message());
        }
        
        @Test
        void 충전_후_최대_잔액_초과_시_실패() {
            // given
            UserPoint userPoint = new UserPoint(1L, 1L, UserPoint.MAX_BALANCE - 1000);
            int amount = 2000;
            
            // when
            Throwable throwable = catchThrowable(() -> userPoint.charge(amount));
            
            // then
            assertThat(throwable).isInstanceOf(DomainException.class)
                    .hasFieldOrPropertyWithValue("status", EXCEEDED_MAX_USER_POINT)
                    .hasMessage(EXCEEDED_MAX_USER_POINT.message());
        }
        
        @ParameterizedTest
        @ValueSource(ints = {
            UserPoint.MIN_CHARGE_AMOUNT,
            (UserPoint.MIN_CHARGE_AMOUNT + UserPoint.MAX_CHARGE_AMOUNT) / 2,
            UserPoint.MAX_CHARGE_AMOUNT
        })
        void 유효한_금액_충전_시_성공(int chargeAmount) {
            // given
            UserPoint userPoint = new UserPointFixture()
                .setAmount(0)
                .create();
            int resultAmount = userPoint.amount() + chargeAmount;
            
            // when
            userPoint.charge(chargeAmount);
            
            // then
            assertThat(userPoint.amount()).isEqualTo(resultAmount);
        }
    }
} 