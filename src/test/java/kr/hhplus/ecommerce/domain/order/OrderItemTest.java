package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Nested
    @DisplayName("OrderItem 생성 테스트")
    class ConstructorTest {
        @Test
        void 성공() {
            // given
            long productOptionId = 1L;
            int productPrice = 10000;
            int quantity = 2;

            // when
            OrderItem orderItem = new OrderItem(productOptionId, productPrice, quantity);

            // then
            assertThat(orderItem.productOptionId()).isEqualTo(productOptionId);
            assertThat(orderItem.productPrice()).isEqualTo(productPrice);
            assertThat(orderItem.quantity()).isEqualTo(quantity);
            assertThat(orderItem.amount()).isEqualTo(productPrice * quantity);
        }

        @Test
        void ProductOption_포함한_생성자_성공() {
            // given
            ProductOption productOption = new ProductOptionFixture().create();
            int quantity = 2;

            // when
            OrderItem orderItem = new OrderItem(productOption, quantity);

            // then
            assertThat(orderItem.productOptionId()).isEqualTo(productOption.id());
            assertThat(orderItem.productPrice()).isEqualTo(productOption.price());
            assertThat(orderItem.quantity()).isEqualTo(quantity);
            assertThat(orderItem.amount()).isEqualTo(productOption.price() * quantity);
        }
    }
}