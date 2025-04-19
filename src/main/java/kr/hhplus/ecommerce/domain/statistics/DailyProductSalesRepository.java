package kr.hhplus.ecommerce.domain.statistics;

import java.time.LocalDate;
import java.util.List;

public interface DailyProductSalesRepository {
    List<DailyProductSales> aggregate(LocalDate date, List<Long> productIds);
    void createAll(List<DailyProductSales> dailyProductSalesList);
    void deleteAllByAggregationDate(LocalDate aggregationDate);
    List<Long> findTopSellingProductIds(LocalDate from, LocalDate to, int limit);
}