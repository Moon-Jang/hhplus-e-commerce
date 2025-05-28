package kr.hhplus.ecommerce.interfaces.data;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.infrastructure.external.DataPlatFormClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class DataOrderEventListener {
    private final DataPlatFormClient dataPlatFormClient;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void listen(OrderEvent.Completed event) {
        dataPlatFormClient.sendOrder(event.order().id());
    }
}