package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderFixture;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @InjectMocks
    private PaymentService service;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @Nested
    @DisplayName("결제 테스트")
    class PayTest {
        @Test
        void 결제_성공() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .create();
            Payment payment = new PaymentFixture().create();
            PaymentCommand.Pay command = new PaymentCommand.Pay(order.id());
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));
            given(paymentRepository.save(any(Payment.class))).willReturn(payment);
            
            // when
            service.pay(command);
            
            // then
            verify(orderRepository).findById(command.orderId());
            ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository).save(captor.capture());
            Payment capturedPayment = captor.getValue();
            assertThat(capturedPayment).isNotNull();
            assertThat(capturedPayment.orderId()).isEqualTo(order.id());
            assertThat(capturedPayment.userId()).isEqualTo(order.userId());
            assertThat(capturedPayment.amount()).isEqualTo(order.priceDetails().finalAmount());
        }
        
        @Test
        void 주문이_존재하지_않을_때_실패() {
            // given
            PaymentCommand.Pay command = new PaymentCommand.Pay(1L);
            given(orderRepository.findById(command.orderId())).willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> service.pay(command));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", ORDER_NOT_FOUND)
                .hasMessage(ORDER_NOT_FOUND.message());
            verify(orderRepository).findById(command.orderId());
            verify(paymentRepository, never()).save(any());
        }
        
        @Test
        void 주문_상태가_PENDING이_아닐_때_실패() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.COMPLETED)
                .create();
            PaymentCommand.Pay command = new PaymentCommand.Pay(order.id());
            Payment payment = new PaymentFixture().create();
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));

            // when
            Throwable throwable = catchThrowable(() -> service.pay(command));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", ORDER_NOT_FOUND)
                .hasMessage(ORDER_NOT_FOUND.message());
            verify(orderRepository).findById(command.orderId());
            verify(paymentRepository, never()).save(any());
        }
    }


}