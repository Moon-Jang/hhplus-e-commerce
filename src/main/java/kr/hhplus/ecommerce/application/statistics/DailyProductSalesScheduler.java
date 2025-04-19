package kr.hhplus.ecommerce.application.statistics;

import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyProductSalesScheduler {

    private final DailyProductSalesService dailyProductSalesService;
    
    /**
     * 매일 자정에 실행되어 전날의 판매 통계를 집계합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @SchedulerLock(name = "aggregateDailyProductSales", lockAtLeastFor = "PT10M")
    public void aggregateDailyProductSales() {
        log.info("일일 상품 판매 통계 집계 작업 시작");
        // 환불 건이 존재할 수 있음으로 일주일 이전 데이터부터 새로 집계
        LocalDate from = LocalDate.now().minusDays(8);
        LocalDate to = LocalDate.now().minusDays(1);

        try {
            dailyProductSalesService.aggregate(from, to);
            log.info("일일 상품 판매 통계 집계 작업 완료");
        } catch (Exception e) {
            log.error("일일 상품 판매 통계 집계 작업 실패", e);
        }
    }
} 