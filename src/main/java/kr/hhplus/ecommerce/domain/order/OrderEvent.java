package kr.hhplus.ecommerce.domain.order;

public class OrderEvent {
    public record Completed(long orderId) {
    }
}