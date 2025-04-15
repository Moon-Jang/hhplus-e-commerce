package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentVo pay(PaymentCommand.Pay command) {
        Order pendingOrder = orderRepository.findById(command.orderId())
            .filter(o -> o.status() == Order.Status.PENDING)
            .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
        Payment payment = createPayment(pendingOrder);

        return PaymentVo.from(
            paymentRepository.save(payment)
        );
    }

    private Payment createPayment(Order order) {
        return new Payment(
            order.id(),
            order.userId(),
            order.priceDetails().finalAmount()
        );
    }
} 