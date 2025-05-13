package kr.hhplus.ecommerce.domain.statistics;

import kr.hhplus.ecommerce.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyProductSalesService {
    static final int CHUNK_SIZE = 1000;
    private final DailyProductSalesRepository dailyProductSalesRepository;
    private final ProductRepository productRepository;

    public List<Long> findTopSellingProductIds(int limit) {
        LocalDate from = LocalDate.now().minusDays(3);
        LocalDate to = LocalDate.now();
        
        return dailyProductSalesRepository.findTopSellingProductIds(from, to, limit);
    }


    @Transactional
    public void aggregate(LocalDate from, LocalDate to) {
        long totalProductCount = productRepository.count();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            dailyProductSalesRepository.deleteAllByAggregationDate(date);

            for (int i = 0; (long) i * CHUNK_SIZE < totalProductCount; i ++) {
                List<Long> productIds = productRepository.findAllIds(i, CHUNK_SIZE);
                var chunk = dailyProductSalesRepository.aggregate(date, productIds);
                dailyProductSalesRepository.createAll(chunk);
            }
        }
    }
}