package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import kr.hhplus.ecommerce.infrastructure.external.DataPlatFormClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final DataPlatFormClient dataPlatFormClient;
    private final DailyProductSalesService dailyProductSalesService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void complete(OrderEvent.Complete event) {
        dailyProductSalesService.applySalesDelta(event.orderId());
        dataPlatFormClient.sendOrder(event.orderId());
    }
}