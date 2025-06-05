package kr.hhplus.ecommerce.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class KafkaTestContainerConfig {
    private static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer(
                DockerImageName
                    .parse("apache/kafka-native:3.8.0")
            )
            .withExposedPorts(9092)
            .withEnv("KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE", "true");

        KAFKA_CONTAINER.start();
    }

    public static KafkaContainer getContainer() {
        return KAFKA_CONTAINER;
    }
}
