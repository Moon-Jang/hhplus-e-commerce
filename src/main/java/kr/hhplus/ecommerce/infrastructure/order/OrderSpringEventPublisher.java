package kr.hhplus.ecommerce.infrastructure.order;

import kr.hhplus.ecommerce.domain.order.OrderEvent;
import kr.hhplus.ecommerce.domain.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(OrderEvent.Completed event) {
        eventPublisher.publishEvent(event);
    }
}
