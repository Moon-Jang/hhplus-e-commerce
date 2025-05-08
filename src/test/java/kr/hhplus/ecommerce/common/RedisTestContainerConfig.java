package kr.hhplus.ecommerce.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration
public class RedisTestContainerConfig {
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379);
        REDIS_CONTAINER.start();
    }

    public static GenericContainer<?> getContainer() {
        return REDIS_CONTAINER;
    }
}
