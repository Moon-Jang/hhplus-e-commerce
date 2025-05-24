package kr.hhplus.ecommerce.domain.order;

public interface OrderEventPublisher {
    void publish(OrderEvent.Completed event);
}