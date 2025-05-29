package kr.hhplus.ecommerce.interfaces;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.infrastructure.client.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class KafkaConsumerTest extends IntegrationTestContext {
    static final String REDIS_KEY = "executed";
    static final String TOPIC_NAME = "test-topic";
    static final String DLT_TOPIC_NAME = "test-topic.dlt";
    @Autowired
    private KafkaProducer kafkaProducer;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(REDIS_KEY);
    }

    @Test
    void 컨슈머가_메시지를_수신하는지_검증() throws Exception {
        // given
        TestDto testDto = new TestDto(1L, "test-message", false);
        kafkaProducer.send(TOPIC_NAME, testDto);

        // when then
        Awaitility.await()
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(redisTemplate.hasKey(REDIS_KEY)).isTrue());
    }

    @Test
    void DLT_컨슈머가_메시지를_수신하는지_검증() throws Exception {
        // given
        TestDto testDto = new TestDto(1L, "test-message", true);
        kafkaProducer.send(TOPIC_NAME, testDto);

        // when then
        Awaitility.await()
            .pollInterval(100, TimeUnit.MILLISECONDS)
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(redisTemplate.hasKey(REDIS_KEY)).isTrue());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestConsumer testListener(StringRedisTemplate redisTemplate) {
            return new TestConsumer(redisTemplate);
        }

        @Bean
        public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
            return AdminClient.create(kafkaAdmin.getConfigurationProperties());
        }
    }

    @RequiredArgsConstructor
    static class TestConsumer {
        private final StringRedisTemplate redisTemplate;

        @RetryableTopic(
            attempts = "1",
            backoff = @Backoff(delay = 10, multiplier = 2.0),
            dltTopicSuffix = ".dlt",
            include = { RuntimeException.class }
        )
        @KafkaListener(
            topics = TOPIC_NAME,
            groupId = "test-group"
        )
        public void onMessage(@Payload TestDto payload) {
            if (payload.error()) {
                throw new RuntimeException("Error processing message");
            }

            redisTemplate.opsForValue().setIfAbsent(REDIS_KEY, "true");
        }

        @KafkaListener(
            topics = DLT_TOPIC_NAME,
            groupId = "test-group"
        )
        public void onDltMessage(@Payload TestDto payload) {
            redisTemplate.opsForValue().setIfAbsent(REDIS_KEY, "true");
        }
    }

    record TestDto(
        long id,
        String message,
        boolean error
    ) {
    }
}
