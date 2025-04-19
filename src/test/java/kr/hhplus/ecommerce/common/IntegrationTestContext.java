package kr.hhplus.ecommerce.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@Import({MySQLTestContainerConfig.class})
public abstract class IntegrationTestContext {
    static MySQLContainer<?> mySQLContainer = MySQLTestContainerConfig.getContainer();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private CleanUp cleanUp;

    @AfterEach
    void tearDown() {
        cleanUp.all();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    protected void runConcurrent(int threadCount, Runnable task) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }

    protected void runConcurrent(int threadCount, IntConsumer indexedTask) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    indexedTask.accept(index);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }

    protected void runConcurrent(Runnable[] tasks) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(tasks.length);
        CountDownLatch latch = new CountDownLatch(tasks.length);

        for (Runnable task : tasks) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }
}