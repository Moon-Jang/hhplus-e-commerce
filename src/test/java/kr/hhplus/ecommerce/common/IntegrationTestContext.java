package kr.hhplus.ecommerce.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
@Import({MySQLTestContainerConfig.class, RedisTestContainerConfig.class})
public abstract class IntegrationTestContext {
    static MySQLContainer<?> mySQLContainer = MySQLTestContainerConfig.getContainer();
    static GenericContainer<?> redisContainer = RedisTestContainerConfig.getContainer();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private CleanUp cleanUp;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        cleanUp.all();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
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

    protected void withManualSession(Consumer<EntityManager> entityManagerConsumer) {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            entityManagerConsumer.accept(em);
            tx.commit();
        }
    }

    protected <R> R withManualSession(Function<EntityManager,R> entityManagerConsumer) {
        R result;

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            tx.begin();
            result = entityManagerConsumer.apply(em);
            tx.commit();
        }

        return result;
    }

    protected <T> T persistEntity(T entity) {
        Session session = entityManager.unwrap(Session.class);
        StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();
        Transaction transaction = statelessSession.beginTransaction();

        statelessSession.insert(entity);
        transaction.commit();
        statelessSession.close();

        return entity;
    }
}