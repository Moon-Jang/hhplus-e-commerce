package kr.hhplus.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    @Bean
    public TaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // CPU 코어 수 기반으로 풀 크기 계산
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = availableProcessors * 2;
        int maxPoolSize = availableProcessors * 4;

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity());
        executor.setThreadNamePrefix("Async-Executor-");
        executor.initialize();
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    private int queueCapacity() {
        // JVM이 사용할 수 있는 최대 메모리 크기(바이트 단위)
        long maxMemory = Runtime.getRuntime().maxMemory();

        // 메모리 크기를 MB 단위로 변환
        int maxMemoryInMB = (int) (maxMemory / (1024 * 1024));

        // 메모리 크기에 따라 작업 큐 크기 계산
        if (maxMemoryInMB <= 1024) {  // 1GB 이하 메모리
            return 200;      // 작은 큐 크기
        } else if (maxMemoryInMB <= 4096) {  // 1GB ~ 4GB 메모리
            return 500;      // 중간 큐 크기
        } else {  // 4GB 이상 메모리
            return 1000;     // 큰 큐 크기
        }
    }

    @Slf4j
    public static class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable ex,
                                            Method method,
                                            Object... params) {
            log.error("AsyncError: {}", ex.getMessage(), ex);
        }
    }
}