package kr.hhplus.ecommerce.application.data;

import kr.hhplus.ecommerce.application.client.MessageClient;
import kr.hhplus.ecommerce.common.constant.Topics;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPlatFromService {
    public final MessageClient messageClient;

    public void sendOrder(OrderVo order) {
        messageClient.send(Topics.SAVE_ORDER_DATA, order);
    }
}
