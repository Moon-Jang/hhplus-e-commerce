package kr.hhplus.ecommerce.domain.statistics;

import kr.hhplus.ecommerce.domain.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService.CHUNK_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyProductSalesServiceTest {
    @InjectMocks
    private DailyProductSalesService service;
    @Mock
    private DailyProductSalesRepository dailyProductSalesRepository;
    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("인기 상품 ID 조회")
    class FindTopSellingProductIdsTest {
        @Test
        void 인기_상품_조회_성공() {
            // given
            int limit = 5;
            LocalDate from = LocalDate.now().minusMonths(1);
            LocalDate to = LocalDate.now();
            List<Long> expectedProductIds = List.of(1L, 2L, 3L);

            given(dailyProductSalesRepository.findTopSellingProductIds(from, to, limit))
                .willReturn(expectedProductIds);

            // when
            List<Long> result = service.findTopSellingProductIds(limit);

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(expectedProductIds);

            verify(dailyProductSalesRepository).findTopSellingProductIds(from, to, limit);
        }

        @Test
        void 인기_상품이_없을때() {
            // given
            int limit = 5;
            LocalDate from = LocalDate.now().minusMonths(1);
            LocalDate to = LocalDate.now();
            List<Long> emptyProductIds = List.of();

            given(dailyProductSalesRepository.findTopSellingProductIds(from, to, limit))
                .willReturn(emptyProductIds);

            // when
            List<Long> result = service.findTopSellingProductIds(limit);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(dailyProductSalesRepository).findTopSellingProductIds(from, to, limit);
        }
    }

    @Nested
    @DisplayName("상품 판매 통계 집계 테스트")
    class AggregateTest {
        @Test
        void 집계_성공() {
            // given
            LocalDate date = LocalDate.of(2023, 1, 1);
            long totalProductCount = CHUNK_SIZE * 2L;
            List<Long> productIds1 = List.of(1L, 2L, 3L);
            List<Long> productIds2 = List.of(4L, 5L, 6L);
            
            List<DailyProductSales> chunk1 = List.of(
                new DailyProductSalesFixture().setProductId(1L).setOrderCount(10L).create(),
                new DailyProductSalesFixture().setProductId(2L).setOrderCount(5L).create(),
                new DailyProductSalesFixture().setProductId(3L).setOrderCount(8L).create()
            );
            
            List<DailyProductSales> chunk2 = List.of(
                new DailyProductSalesFixture().setProductId(4L).setOrderCount(3L).create(),
                new DailyProductSalesFixture().setProductId(5L).setOrderCount(7L).create(),
                new DailyProductSalesFixture().setProductId(6L).setOrderCount(2L).create()
            );
            
            given(productRepository.count()).willReturn(totalProductCount);
            given(productRepository.findAllIds(0, CHUNK_SIZE)).willReturn(productIds1);
            given(productRepository.findAllIds(1, CHUNK_SIZE)).willReturn(productIds2);
            given(dailyProductSalesRepository.aggregate(date, productIds1)).willReturn(chunk1);
            given(dailyProductSalesRepository.aggregate(date, productIds2)).willReturn(chunk2);
            
            // when
            service.aggregate(date, date);
            
            // then
            verify(dailyProductSalesRepository).deleteAllByAggregationDate(date);
            verify(productRepository).count();
            verify(productRepository).findAllIds(0, CHUNK_SIZE);
            verify(productRepository).findAllIds(1, CHUNK_SIZE);
            verify(dailyProductSalesRepository).aggregate(date, productIds1);
            verify(dailyProductSalesRepository).aggregate(date, productIds2);
            verify(dailyProductSalesRepository).createAll(chunk1);
            verify(dailyProductSalesRepository).createAll(chunk2);
            verify(productRepository, times(2)).findAllIds(anyInt(), anyInt());
            verify(dailyProductSalesRepository, times(2)).aggregate(any(), any());
            verify(dailyProductSalesRepository, times(2)).createAll(any());
        }
        
        @Test
        void 상품이_없는_경우() {
            // given
            LocalDate date = LocalDate.of(2023, 1, 1);
            long totalProductCount = 0L;
            
            given(productRepository.count()).willReturn(totalProductCount);
            
            // when
            service.aggregate(date, date);
            
            // then
            verify(dailyProductSalesRepository).deleteAllByAggregationDate(date);
            verify(productRepository).count();
            // 상품이 없으므로 findAllIds, aggregate, createAll은 호출되지 않아야 함
            verify(productRepository, never()).findAllIds(anyInt(), anyInt());
            verify(dailyProductSalesRepository, never()).aggregate(any(), any());
            verify(dailyProductSalesRepository, never()).createAll(any());
        }
    }
} 