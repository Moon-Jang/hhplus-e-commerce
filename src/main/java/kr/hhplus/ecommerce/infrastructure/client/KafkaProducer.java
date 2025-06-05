package kr.hhplus.ecommerce.infrastructure.client;

import kr.hhplus.ecommerce.application.client.MessageClient;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer implements MessageClient {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public <T> void send(String topic, T message) {
        kafkaTemplate.send(topic, JsonUtils.stringify(message));
    }

    @Override
    public <T> void send(String topic, String partitionKey, T message) {
        kafkaTemplate.send(topic, partitionKey, JsonUtils.stringify(message));
    }
}