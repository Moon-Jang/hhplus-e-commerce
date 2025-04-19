package kr.hhplus.ecommerce.application.product;

import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVo;
import kr.hhplus.ecommerce.domain.product.ProductVoFixture;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {
    @InjectMocks
    private ProductFacade productFacade;
    @Mock
    private ProductService productService;
    @Mock
    private DailyProductSalesService dailyProductSalesService;

    @Nested
    @DisplayName("인기 상품 목록 조회")
    class FindTopSellingProductsTest {

        @Test
        @DisplayName("인기 상품을 성공적으로 조회한다")
        void 성공() {
            // given
            int limit = 5;
            List<Long> productIds = Arrays.asList(1L, 2L, 3L);
            
            ProductVo product1 = new ProductVoFixture().setId(1L).setName("인기 상품 1").create();
            ProductVo product2 = new ProductVoFixture().setId(2L).setName("인기 상품 2").create();
            ProductVo product3 = new ProductVoFixture().setId(3L).setName("인기 상품 3").create();
            List<ProductVo> products = List.of(product1, product2, product3);
            
            when(dailyProductSalesService.findTopSellingProductIds(limit)).thenReturn(productIds);
            when(productService.findAllById(productIds)).thenReturn(products);
            
            // when
            List<ProductVo> result = productFacade.findTopSellingProducts(limit);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result.get(0).id()).isEqualTo(1L);
            assertThat(result.get(1).id()).isEqualTo(2L);
            assertThat(result.get(2).id()).isEqualTo(3L);
            
            verify(dailyProductSalesService).findTopSellingProductIds(limit);
            verify(productService).findAllById(productIds);
        }

        @Test
        @DisplayName("인기 상품이 없어도 빈 목록을 반환한다")
        void 인기_상품이_없을때() {
            // given
            int limit = 5;
            List<Long> emptyProductIds = List.of();
            
            when(dailyProductSalesService.findTopSellingProductIds(limit)).thenReturn(emptyProductIds);
            when(productService.findAllById(emptyProductIds)).thenReturn(List.of());
            
            // when
            List<ProductVo> result = productFacade.findTopSellingProducts(limit);
            
            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
            
            verify(dailyProductSalesService).findTopSellingProductIds(limit);
            verify(productService).findAllById(emptyProductIds);
        }
    }
} 