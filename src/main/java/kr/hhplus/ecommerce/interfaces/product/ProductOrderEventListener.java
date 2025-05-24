package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class ProductOrderEventListener {
    private final DailyProductSalesService dailyProductSalesService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void listen(OrderEvent.Completed event) {
        dailyProductSalesService.applySalesDelta(event.orderId());
    }
}
