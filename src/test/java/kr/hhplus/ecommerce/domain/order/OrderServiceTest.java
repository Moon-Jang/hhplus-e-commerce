package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.coupon.CouponFixture;
import kr.hhplus.ecommerce.domain.coupon.IssuedCoupon;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponFixture;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponRepository;
import kr.hhplus.ecommerce.domain.product.*;
import kr.hhplus.ecommerce.infrastructure.external.DataPlatFormClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService service;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;
    @Mock
    private DataPlatFormClient dataPlatFormClient;
    
    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateTest {
        @Test
        void 쿠폰없이_주문_성공() {
            // given
            Product product = new ProductFixture().create();
            List<ProductOption> productOptions = List.of(
                new ProductOptionFixture().setId(1L).setProduct(product).create(),
                new ProductOptionFixture().setId(2L).setProduct(product).create()
            );
            List<Long> productOptionIds = productOptions.stream().map(ProductOption::id).toList();
            OrderCommand.Create command = new CommandFixture()
                .setItems(List.of(
                    new OrderCommand.Create.OrderItem(productOptions.get(0).id(), 2),
                    new OrderCommand.Create.OrderItem(productOptions.get(1).id(), 1)
                ))
                .create();
            given(productOptionRepository.findAllByIds(productOptionIds)).willReturn(productOptions);
            given(orderRepository.save(any(Order.class))).willReturn(new OrderFixture().create());
            
            // when
            service.create(command);
            
            // then
            verify(productOptionRepository).findAllByIds(productOptionIds);
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());
            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.userId()).isEqualTo(command.userId());
            assertThat(capturedOrder.items()).hasSize(2);
            assertThat(capturedOrder.items().get(0).productOptionId()).isEqualTo(productOptions.get(0).id());
            assertThat(capturedOrder.items().get(0).productPrice()).isEqualTo(productOptions.get(0).product().price());
            assertThat(capturedOrder.items().get(0).quantity()).isEqualTo(command.items().get(0).quantity());
            assertThat(capturedOrder.items().get(0).amount()).isEqualTo(productOptions.get(0).product().price() * command.items().get(0).quantity());
            assertThat(capturedOrder.items().get(1).productOptionId()).isEqualTo(productOptions.get(1).id());
            assertThat(capturedOrder.items().get(1).productPrice()).isEqualTo(productOptions.get(1).product().price());
            assertThat(capturedOrder.items().get(1).quantity()).isEqualTo(command.items().get(1).quantity());
            assertThat(capturedOrder.items().get(1).amount()).isEqualTo(productOptions.get(1).product().price() * command.items().get(1).quantity());
            assertThat(capturedOrder.priceDetails().totalAmount().intValue()).isEqualTo(capturedOrder.items().stream().mapToLong(OrderItem::amount).sum());
            assertThat(capturedOrder.priceDetails().discountAmount().intValue()).isEqualTo(0);
            assertThat(capturedOrder.priceDetails().finalAmount().intValue()).isEqualTo(capturedOrder.priceDetails().totalAmount().subtract(capturedOrder.priceDetails().discountAmount()).intValue());
            assertThat(capturedOrder.issuedCouponId()).isNull();
            assertThat(capturedOrder.status()).isEqualTo(Order.Status.PENDING);
        }
        
        @Test
        void 쿠폰적용_주문_성공() {
            // given
            Product product = new ProductFixture().create();
            List<ProductOption> productOptions = List.of(
                new ProductOptionFixture().setId(1L).setProduct(product).create()
            );
            List<Long> productOptionIds = productOptions.stream().map(ProductOption::id).toList();
            IssuedCoupon issuedCoupon = new IssuedCouponFixture()
                .setCoupon(new CouponFixture().setDiscountAmount(100).create())
                .create();
            OrderCommand.Create command = new CommandFixture()
                .setItems(List.of(
                    new OrderCommand.Create.OrderItem(productOptions.get(0).id(), 2)
                ))
                .setIssuedCouponId(Optional.of(issuedCoupon.id()))
                .create();
            given(issuedCouponRepository.findById(issuedCoupon.id())).willReturn(Optional.of(issuedCoupon));
            given(productOptionRepository.findAllByIds(productOptionIds)).willReturn(productOptions);
            given(orderRepository.save(any(Order.class))).willReturn(new OrderFixture().create());
            
            // when
            service.create(command);
            
            // then
            verify(issuedCouponRepository).findById(issuedCoupon.id());
            verify(productOptionRepository).findAllByIds(productOptionIds);
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());
            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.userId()).isEqualTo(command.userId());
            assertThat(capturedOrder.items()).hasSize(1);
            assertThat(capturedOrder.items().get(0).productOptionId()).isEqualTo(productOptions.get(0).id());
            assertThat(capturedOrder.items().get(0).productPrice()).isEqualTo(productOptions.get(0).product().price());
            assertThat(capturedOrder.items().get(0).quantity()).isEqualTo(command.items().get(0).quantity());
            assertThat(capturedOrder.items().get(0).amount()).isEqualTo(productOptions.get(0).product().price() * command.items().get(0).quantity());
            assertThat(capturedOrder.priceDetails().totalAmount().intValue()).isEqualTo(capturedOrder.items().stream().mapToLong(OrderItem::amount).sum());
            assertThat(capturedOrder.priceDetails().discountAmount().intValue()).isEqualTo(issuedCoupon.coupon().discountAmount());
            assertThat(capturedOrder.priceDetails().finalAmount().intValue()).isEqualTo(capturedOrder.priceDetails().totalAmount().subtract(capturedOrder.priceDetails().discountAmount()).intValue());
            assertThat(capturedOrder.issuedCouponId()).isEqualTo(issuedCoupon.id());
            assertThat(capturedOrder.status()).isEqualTo(Order.Status.PENDING);
        }

        @Getter
        @Setter
        @Accessors(chain = true)
        private static class CommandFixture implements TestFixture<OrderCommand.Create> {
            private long userId = 1L;
            private List<OrderCommand.Create.OrderItem> items = List.of(
                new OrderCommand.Create.OrderItem(1L, 2),
                new OrderCommand.Create.OrderItem(2L, 1)
            );
            private Optional<Long> issuedCouponId = Optional.empty();

            public OrderCommand.Create create() {
                return new OrderCommand.Create(
                    userId,
                    items,
                    issuedCouponId
                );
            }
        }
    }

    @Nested
    @DisplayName("주문 완료 테스트")
    class CompleteTest {
        @Test
        void 주문_완료_성공() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .create();
            OrderCommand.Complete command = new OrderCommand.Complete(order.id());
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));
            given(orderRepository.save(any(Order.class))).willReturn(order);
            
            // when
            service.complete(command);
            
            // then
            ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
            verify(orderRepository).save(orderCaptor.capture());
            verify(dataPlatFormClient).sendOrderAsync(order.id());
            Order capturedOrder = orderCaptor.getValue();
            assertThat(capturedOrder.id()).isEqualTo(order.id());
            assertThat(capturedOrder.status()).isEqualTo(Order.Status.COMPLETED);
        }

        @Test
        void 주문_존재하지_않을_때_실패() {
            // given
            long notExistOrderId = 1L;
            OrderCommand.Complete command = new OrderCommand.Complete(notExistOrderId);
            given(orderRepository.findById(command.orderId())).willReturn(Optional.empty());
            
            // when
            Throwable throwable = catchThrowable(() -> service.complete(command));
            
            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", ORDER_NOT_FOUND)
                .hasMessage(ORDER_NOT_FOUND.message());
            verify(orderRepository).findById(command.orderId());
            verify(orderRepository, times(0)).save(any(Order.class));
            verify(dataPlatFormClient, times(0)).sendOrderAsync(anyLong());
        }
    }
}