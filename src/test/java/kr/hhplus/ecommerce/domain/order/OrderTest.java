package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.domain.common.Money;
import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponFixture;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;

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
            ProductOption productOption = new ProductOptionFixture().create();
            int quantity = 3;
            
            // when
            Order order = new Order(userId);
            order.addItem(productOption, quantity);
            
            // then
            assertThat(order.userId()).isEqualTo(userId);
            assertThat(order.items()).hasSize(1);
            assertThat(order.items().get(0).productOptionId()).isEqualTo(productOption.id());
            assertThat(order.items().get(0).quantity()).isEqualTo(quantity);
            assertThat(order.items().get(0).amount()).isEqualTo(productOption.price() * quantity);
            assertThat(order.priceDetails().totalAmount()).isEqualTo(Money.wons(productOption.price() * quantity));
            assertThat(order.priceDetails().discountAmount()).isEqualTo(Money.ZERO);
            assertThat(order.priceDetails().finalAmount()).isEqualTo(Money.wons(productOption.price() * quantity));
            assertThat(order.status()).isEqualTo(Order.Status.PENDING);
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

    /* 
    public void applyCoupon(IssuedCoupon issuedCoupon) {
        this.issuedCouponId = issuedCoupon.id();
        this.price.addDiscount(issuedCoupon.discountAmount());
    }
    */

    @Nested
    @DisplayName("쿠폰 적용 테스트")
    class ApplyCouponTest {
        @Test
        void 쿠폰_적용_성공() {
            // given
            OrderPriceDetails price = new OrderPriceFixture()
                .setTotalAmount(Money.wons(10000L))
                .setDiscountAmount(Money.ZERO)
                .setFinalAmount(Money.wons(10000L))
                .create();
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .setPriceDetails(price)
                .create();
            IssuedCoupon issuedCoupon = new IssuedCouponFixture().create();
            Money expectedDiscountAmount = price.discountAmount().plus(Money.wons(issuedCoupon.discountAmount()));
            Money expectedFinalAmount = price.totalAmount().minus(expectedDiscountAmount);

            // when
            order.applyCoupon(issuedCoupon);

            // then
            assertThat(order.issuedCouponId()).isEqualTo(issuedCoupon.id());
            assertThat(order.priceDetails().discountAmount()).isEqualTo(expectedDiscountAmount);
            assertThat(order.priceDetails().finalAmount()).isEqualTo(expectedFinalAmount);
        }
    }

    @Nested
    @DisplayName("주문 항목 추가 테스트")
    class AddItemTest {
        @Test
        void 주문_항목_추가_성공() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .setItems(new ArrayList<>())
                .create();
            ProductOption productOption = new ProductOptionFixture().create();
            int quantity = 1;

            // when
            order.addItem(productOption, quantity);

            // then
            assertThat(order.items()).hasSize(1);
            assertThat(order.items().get(0).productOptionId()).isEqualTo(productOption.id());
            assertThat(order.items().get(0).quantity()).isEqualTo(quantity);
            assertThat(order.items().get(0).amount()).isEqualTo(productOption.price() * quantity);
        }
    }
}