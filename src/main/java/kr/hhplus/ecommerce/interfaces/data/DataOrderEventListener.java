package kr.hhplus.ecommerce.interfaces.data;

import kr.hhplus.ecommerce.application.data.DataPlatFromService;
import kr.hhplus.ecommerce.domain.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class DataOrderEventListener {
    private final DataPlatFromService dataPlatFromService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void listen(OrderEvent.Completed event) {
        dataPlatFromService.sendOrder(event.order());
    }
}