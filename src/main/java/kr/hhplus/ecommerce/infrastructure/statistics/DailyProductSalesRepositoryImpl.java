package kr.hhplus.ecommerce.infrastructure.statistics;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSales;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesRepository;
import kr.hhplus.ecommerce.infrastructure.statistics.DailyProductSalesRedisRepository.AggregationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.ecommerce.domain.order.QOrder.order;
import static kr.hhplus.ecommerce.domain.order.QOrderItem.orderItem;
import static kr.hhplus.ecommerce.domain.product.QProductOption.productOption;
import static kr.hhplus.ecommerce.infrastructure.statistics.QDailyProductSalesRdbEntity.dailyProductSalesRdbEntity;

@Repository
@RequiredArgsConstructor
public class DailyProductSalesRepositoryImpl implements DailyProductSalesRepository {
    private final JPAQueryFactory queryFactory;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DailyProductSalesRedisRepository redisRepository;

    @Override
    public List<DailyProductSales> aggregate(LocalDate aggregationDate, List<Long> productIds) {
        return queryFactory.select(Projections.constructor(
                DailyProductSales.class,
                Expressions.constant(aggregationDate),
                productOption.product.id,
                orderItem.count()
            ))
            .from(order)
            .join(order.items, orderItem)
            .join(productOption).on(orderItem.productOptionId.eq(productOption.id))
            .where(
                order.createdAt.between(aggregationDate.atStartOfDay(), aggregationDate.plusDays(1).atStartOfDay()),
                order.status.eq(Order.Status.COMPLETED),
                productOption.product.id.in(productIds)
            )
            .groupBy(productOption.product.id)
            .fetch();
    }

    @Override
    public void createAll(List<DailyProductSales> list) {
        if (list.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
            """
            INSERT INTO daily_product_sales (aggregation_date, product_id, order_count, created_at, updated_at)
            VALUES (:aggregationDate, :productId, :orderCount, NOW(), NOW())
            """,
            list.stream()
                .map(DailyProductSalesRdbEntity::from)
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new)
        );
    }

    @Override
    public void saveDelta(DailyProductSales delta) {
        redisRepository.saveDelta(delta);
    }

    @Override
    public void deleteAllByAggregationDate(LocalDate aggregationDate) {
        String sql = "DELETE FROM daily_product_sales WHERE aggregation_date = :aggregationDate";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("aggregationDate", aggregationDate);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<Long> findTopSellingProductIds(LocalDate from, LocalDate to, int limit) {
        return queryFactory
            .select(dailyProductSalesRdbEntity.productId)
            .from(dailyProductSalesRdbEntity)
            .where(
                dailyProductSalesRdbEntity.aggregationDate.between(from, to),
                dailyProductSalesRdbEntity.orderCount.gt(0)
            )
            .groupBy(
                dailyProductSalesRdbEntity.productId
            )
            .orderBy(
                dailyProductSalesRdbEntity.orderCount.sum().desc()
            )
            .limit(limit)
            .fetch();
    }
}