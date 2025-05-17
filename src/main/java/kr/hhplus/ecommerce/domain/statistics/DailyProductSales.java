package kr.hhplus.ecommerce.domain.statistics;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Accessors(fluent = true)
public class DailyProductSales {
    private final LocalDate aggregationDate;
    private final long productId;
    private final long orderCount;
    private final long countDelta; // 변화량

    public DailyProductSales(LocalDate aggregationDate,
                             long productId,
                             long orderCount,
                             long countDelta) {
        this.aggregationDate = aggregationDate;
        this.productId = productId;
        this.orderCount = orderCount;
        this.countDelta = countDelta;
    }

    public static DailyProductSales createDelta(LocalDate aggregationDate,
                                                long productId,
                                                long countDelta) {
        return new DailyProductSales(aggregationDate, productId, 0, countDelta);
    }
}