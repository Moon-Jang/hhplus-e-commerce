package kr.hhplus.ecommerce.domain.statistics;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class DailyProductSalesFixture implements TestFixture<DailyProductSales> {
    private LocalDate aggregationDate = LocalDate.of(2023, 1, 1);
    private long productId = 1L;
    private long orderCount = 10L;

    @Override
    public DailyProductSales create() {
        return new DailyProductSales(
            aggregationDate,
            productId,
            orderCount,
            0L
        );
    }
} 