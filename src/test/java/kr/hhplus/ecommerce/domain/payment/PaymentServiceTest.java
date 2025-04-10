package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderFixture;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentService service;
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    private PaymentFailureHistoryRepository paymentFailureHistoryRepository;
    private PaymentStrategy pointPaymentStrategy;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentFailureHistoryRepository = mock(PaymentFailureHistoryRepository.class);
        pointPaymentStrategy = spy(new TestPaymentStrategy());
        List<PaymentStrategy> paymentStrategies = List.of(pointPaymentStrategy);
        service = new PaymentService(orderRepository, paymentRepository, paymentFailureHistoryRepository, paymentStrategies);
    }

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
            PaymentCommand.Pay command = new PaymentCommand.Pay(order.id(), payment.method());
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));
            given(paymentRepository.save(any(Payment.class))).willReturn(payment);
            
            // when
            service.pay(command);
            
            // then
            verify(orderRepository).findById(command.orderId());
            verify(pointPaymentStrategy).isSupported(command.payMethod());
            verify(pointPaymentStrategy).process(any(Payment.class));
            ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
            verify(paymentRepository).save(captor.capture());
            Payment capturedPayment = captor.getValue();
            assertThat(capturedPayment).isNotNull();
            assertThat(capturedPayment.orderId()).isEqualTo(order.id());
            assertThat(capturedPayment.userId()).isEqualTo(order.userId());
            assertThat(capturedPayment.amount()).isEqualTo(order.finalAmount());
            assertThat(capturedPayment.method()).isEqualTo(command.payMethod());
        }

        @Test
        void 결제_금액이_0원이면_결제_모듈_스킵() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.PENDING)
                .setFinalAmount(0)
                .create();
            Payment payment = new PaymentFixture().create();
            PaymentCommand.Pay command = new PaymentCommand.Pay(order.id(), Payment.Method.POINT);
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));
            given(paymentRepository.save(any(Payment.class))).willReturn(payment);

            // when
            service.pay(command);

            // then
            verify(orderRepository).findById(command.orderId());
            verify(paymentRepository).save(any());
            verify(pointPaymentStrategy, never()).isSupported(command.payMethod());
            verify(pointPaymentStrategy, never()).process(any(Payment.class));
        }
        
        @Test
        void 주문이_존재하지_않을_때_실패() {
            // given
            PaymentCommand.Pay command = new PaymentCommand.Pay(1L, Payment.Method.POINT);
            given(orderRepository.findById(command.orderId())).willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> service.pay(command));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", ORDER_NOT_FOUND)
                .hasMessage(ORDER_NOT_FOUND.message());
            verify(orderRepository).findById(command.orderId());
            verify(paymentRepository, never()).save(any());
            verify(pointPaymentStrategy, never()).isSupported(command.payMethod());
            verify(pointPaymentStrategy, never()).process(any(Payment.class));
        }
        
        @Test
        void 주문_상태가_PENDING이_아닐_때_실패() {
            // given
            Order order = new OrderFixture()
                .setStatus(Order.Status.COMPLETED)
                .create();
            PaymentCommand.Pay command = new PaymentCommand.Pay(order.id(), Payment.Method.POINT);
            given(orderRepository.findById(command.orderId())).willReturn(Optional.of(order));

            // when
            Throwable throwable = catchThrowable(() -> service.pay(command));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", ORDER_NOT_FOUND)
                .hasMessage(ORDER_NOT_FOUND.message());
            verify(orderRepository).findById(command.orderId());
            verify(paymentRepository, never()).save(any());
            verify(pointPaymentStrategy, never()).isSupported(command.payMethod());
            verify(pointPaymentStrategy, never()).process(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("결제 실패 내역 저장 테스트")
    class SavePaymentFailureHistoryTest {
        @Test
        void 결제_실패_히스토리_저장() {
            // given
            Payment payment = new PaymentFixture().create();
            PaymentCommand.SaveFailureHistory command = new PaymentCommand.SaveFailureHistory(
                payment.userId(),
                payment.amount(),
                payment.method(),
                "결제 실패 사유"
            );
            PaymentFailureHistory paymentFailureHistory = new PaymentFailureHistory(
                payment.userId(),
                payment.method(),
                payment.amount(),
                "결제 실패 사유"
            );
            given(paymentFailureHistoryRepository.save(any())).willReturn(paymentFailureHistory);

            // when
            service.saveFailureHistory(command);

            // then
            ArgumentCaptor<PaymentFailureHistory> captor = ArgumentCaptor.forClass(PaymentFailureHistory.class);
            verify(paymentFailureHistoryRepository).save(captor.capture());
            PaymentFailureHistory captured = captor.getValue();
            assertThat(captured).isNotNull();
            assertThat(captured.userId()).isEqualTo(command.userId());
            assertThat(captured.amount()).isEqualTo(command.amount());
            assertThat(captured.payMethod()).isEqualTo(command.payMethod());
            assertThat(captured.reason()).isEqualTo(command.failedReason());
        }
    }

    private static class TestPaymentStrategy implements PaymentStrategy {
        @Override
        public boolean isSupported(Payment.Method payMethod) {
            return true;
        }

        @Override
        public void process(Payment payment) {
            // Do nothing
        }
    }
}