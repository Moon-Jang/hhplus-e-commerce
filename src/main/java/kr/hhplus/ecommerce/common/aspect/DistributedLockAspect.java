package kr.hhplus.ecommerce.common.aspect;

import kr.hhplus.ecommerce.common.support.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";
    private final RedissonClient redissonClient;

    @Around("@annotation(dl)")
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock dl) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(
            signature.getParameterNames(),
            joinPoint.getArgs(),
            dl.key()
        );
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean acquired = rLock.tryLock(dl.waitTime(), dl.leaseTime(), dl.timeUnit());
            log.info("Redisson Lock {} {} {}", signature.getMethod().getName(), key, acquired);
            if (!acquired) {
                throw new IllegalStateException("Lock acquisition failed key: " + key);
            }

            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {} {}", signature.getMethod().getName(), key);
            }
        }
    }
}
