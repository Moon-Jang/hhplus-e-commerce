package kr.hhplus.ecommerce.infrastructure.external;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DataPlatFormClient {

    /*
     * 주문 정보를 외부 데이터 플랫폼에 전송합니다.
     */
    @Async
    public void sendOrderAsync(long orderId) {
        // not implemented
    }
}
