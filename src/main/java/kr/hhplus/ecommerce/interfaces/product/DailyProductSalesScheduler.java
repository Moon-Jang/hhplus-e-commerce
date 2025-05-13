package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.application.product.ProductFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyProductSalesScheduler {
    private final ProductFacade productFacade;

    @Scheduled(cron = "0/10 0 * * * ?", zone = "Asia/Seoul")
    @SchedulerLock(name = "refreshTopSellingProducts", lockAtLeastFor = "PT8S")
    public void refreshTopSellingProducts() {
        try {
            productFacade.refreshTopSellingProducts(10);
        } catch (Exception e) {
            log.error("Failed to refresh top selling products", e);
        }
    }
} 