package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.PAYMENT_METHOD_NOT_SUPPORTED;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentFailureHistoryRepository paymentFailureHistoryRepository;
    private final List<PaymentStrategy> paymentStrategies;
    
    @Transactional
    public PaymentVo pay(PaymentCommand.Pay command) {
        Order pendingOrder = orderRepository.findById(command.orderId())
            .filter(o -> o.status() == Order.Status.PENDING)
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
        Payment payment = createPayment(pendingOrder, command);

        if (payment.amount() > 0) {
            paymentStrategies.stream()
                .filter(strategy -> strategy.isSupported(payment.method()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(PAYMENT_METHOD_NOT_SUPPORTED))
                .process(payment);
        }

        return PaymentVo.from(
            paymentRepository.save(payment)
        );
    }

    @Transactional
    public void saveFailureHistory(PaymentCommand.SaveFailureHistory command) {
        PaymentFailureHistory history = new PaymentFailureHistory(
            command.userId(),
            command.payMethod(),
            command.amount(),
            command.failedReason()
        );
        paymentFailureHistoryRepository.save(history);
    }

    private Payment createPayment(Order order, PaymentCommand.Pay command) {
        return new Payment(
            order.id(),
            order.userId(),
            order.finalAmount(),
            command.payMethod()
        );
    }
} 