package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.infrastructure.external.DataPlatFormClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderApplicationEventListener {
    private final DataPlatFormClient dataPlatFormClient;
    private final PaymentService paymentService;


    @Async
    @TransactionalEventListener
    public void handleOrderComplete(OrderApplicationEvent.Complete event) {
        OrderVo order = event.order();
        dataPlatFormClient.sendOrder(order);
    }

    @Async
    @EventListener
    public void handlePaymentFailure(OrderApplicationEvent.PaymentFailure event) {
        OrderVo order = event.order();

        paymentService.saveFailureHistory(
            new PaymentCommand.SaveFailureHistory(
                order.userId(),
                order.finalAmount(),
                event.payMethod(),
                event.failedReason()
            )
        );
    }
}
