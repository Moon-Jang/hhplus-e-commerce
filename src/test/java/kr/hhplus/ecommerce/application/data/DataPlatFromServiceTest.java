package kr.hhplus.ecommerce.application.data;

import kr.hhplus.ecommerce.application.client.MessageClient;
import kr.hhplus.ecommerce.common.constant.Topics;
import kr.hhplus.ecommerce.domain.order.OrderFixture;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataPlatFromServiceTest {
    @InjectMocks
    private DataPlatFromService service;
    @Mock
    private MessageClient messageClient;

    @Test
    @DisplayName("주문 데이터 전송 테스트")
    void sendOrder() {
        // given
        OrderVo order = OrderVo.from(new OrderFixture().create());

        // when
        service.sendOrder(order);

        // then
        ArgumentCaptor<OrderVo> orderCaptor = ArgumentCaptor.forClass(OrderVo.class);
        verify(messageClient).send(eq(Topics.SAVE_ORDER_DATA), orderCaptor.capture());
        OrderVo capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder).isEqualTo(order);
    }
}