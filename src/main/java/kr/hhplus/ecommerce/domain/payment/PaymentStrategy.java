package kr.hhplus.ecommerce.domain.payment;

public interface PaymentStrategy {
    boolean isSupported(Payment.Method payMethod);
    void process(Payment payment);
}