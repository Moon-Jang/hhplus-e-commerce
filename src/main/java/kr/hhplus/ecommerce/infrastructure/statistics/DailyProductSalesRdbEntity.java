package kr.hhplus.ecommerce.infrastructure.statistics;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSales;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Entity(name = "daily_product_sales")
@Table(
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_daily_product_sales_aggregation_date_product_id", columnNames = {"aggregationDate", "productId"}),
    },
    indexes = @Index(name = "dps_covering_index", columnList = "aggregationDate, productId, orderCount")
)
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DailyProductSalesRdbEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate aggregationDate;
    private long productId;
    private long orderCount;

    public DailyProductSalesRdbEntity(LocalDate aggregationDate,
                                      long productId,
                                      long orderCount) {
        this.aggregationDate = aggregationDate;
        this.productId = productId;
        this.orderCount = orderCount;
    }

    public static DailyProductSalesRdbEntity from(DailyProductSales domainEntity) {
        return new DailyProductSalesRdbEntity(
            domainEntity.aggregationDate(),
            domainEntity.productId(),
            domainEntity.orderCount()
        );
    }
}