package kr.hhplus.ecommerce.interfaces.coupon;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueCouponScheduler {
    private final IssuedCouponService issuedCouponService;

    @Scheduled(fixedRate = 2000)
    @SchedulerLock(name = "releaseFromWaitingQueue", lockAtLeastFor = "PT1S")
    public void releaseFromWaitingQueue() {
        try{
            issuedCouponService.releaseFromWaitingQueue();
        } catch (Exception e) {
            log.error("Failed to release from waiting queue", e);
        }
    }
} 