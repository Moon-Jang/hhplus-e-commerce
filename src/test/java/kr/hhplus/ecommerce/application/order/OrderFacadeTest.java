package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderCommand;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.OrderVoFixture;
import kr.hhplus.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.point.UserPointCommand;
import kr.hhplus.ecommerce.domain.point.UserPointService;
import kr.hhplus.ecommerce.domain.product.ProductCommand;
import kr.hhplus.ecommerce.domain.product.ProductService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {
    @InjectMocks
    private OrderFacade facade;
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private IssuedCouponService issuedCouponService;
    @Mock
    private ProductService productService;
    @Mock
    private UserPointService userPointService;

    @Nested
    @DisplayName("주문 처리 테스트")
    class ProcessTest {
        @Test
        void 성공() {
            // given
            OrderCommand.Create command = new CommandFixture().create();
            given(orderService.create(command)).willReturn(new OrderVoFixture()
                .setIssuedCouponId(command.issuedCouponId()).setStatus(Order.Status.PENDING).create());
            given(orderService.complete(any(OrderCommand.Complete.class))).willReturn(new OrderVoFixture().create());
            
            // when
            facade.process(command);
            
            // then
            verify(orderService).create(command);
            verify(productService).deductStock(any(ProductCommand.DeductStock.class));
            verify(issuedCouponService).use(anyLong());
            verify(userPointService).use(any(UserPointCommand.Use.class));
            verify(paymentService).pay(any(PaymentCommand.Pay.class));
            verify(orderService).complete(any(OrderCommand.Complete.class));
        }

        @Test
        void 쿠폰이_없는_경우_성공() {
            // given
            OrderCommand.Create command = new CommandFixture().setIssuedCouponId(Optional.empty()).create();
            given(orderService.create(command)).willReturn(new OrderVoFixture().setStatus(Order.Status.PENDING).create());
            given(orderService.complete(any(OrderCommand.Complete.class))).willReturn(new OrderVoFixture().create());

            // when
            facade.process(command);

            // then
            verify(orderService).create(command);
            verify(productService).deductStock(any(ProductCommand.DeductStock.class));
            verify(issuedCouponService, never()).use(anyLong());
            verify(userPointService).use(any(UserPointCommand.Use.class));
            verify(paymentService).pay(any(PaymentCommand.Pay.class));
            verify(orderService).complete(any(OrderCommand.Complete.class));
        }
        
        @Test
        void 결제_실패시_예외_발생() {
            // given
            OrderCommand.Create command = new CommandFixture().create();
            given(orderService.create(command)).willReturn(new OrderVoFixture()
                .setIssuedCouponId(command.issuedCouponId()).setStatus(Order.Status.PENDING).create());
            given(paymentService.pay(any(PaymentCommand.Pay.class))).willThrow(new RuntimeException("결제 실패"));
            
            // when
            Throwable throwable = catchThrowable(() -> facade.process(command));
            
            // then
            assertThat(throwable).isInstanceOf(RuntimeException.class);
            assertThat(throwable).hasMessage("결제 실패");
            verify(orderService).create(command);
            verify(issuedCouponService).use(anyLong());
            verify(userPointService).use(any(UserPointCommand.Use.class));
            verify(paymentService).pay(any(PaymentCommand.Pay.class));
            verify(productService,never()).deductStock(any(ProductCommand.DeductStock.class));
            verify(orderService, never()).complete(any(OrderCommand.Complete.class));
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
            private Optional<Long> issuedCouponId = Optional.of(1L);

            public OrderCommand.Create create() {
                return new OrderCommand.Create(
                    userId,
                    items,
                    issuedCouponId
                );
            }
        }
    }
}