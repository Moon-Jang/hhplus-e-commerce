package kr.hhplus.ecommerce.interfaces.product;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.domain.order.Order;
import kr.hhplus.ecommerce.domain.order.OrderFixture;
import kr.hhplus.ecommerce.domain.product.Product;
import kr.hhplus.ecommerce.domain.product.ProductFixture;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionFixture;
import kr.hhplus.ecommerce.domain.statistics.DailyProductSalesService;
import kr.hhplus.ecommerce.infrastructure.order.OrderJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductE2ETest extends IntegrationTestContext {
    @Autowired
    private DailyProductSalesService dailyProductSalesService;
    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Nested
    @DisplayName("인기 상품 목록 조회")
    class GetTopSellingProductsTest {
        @Test
        void success() throws Exception {
            //given
            Order order = orderJpaRepository.save(OrderFixture.newEntity().create());
            Product product = persistEntity(new ProductFixture().create());
            ProductOption option = persistEntity(
                new ProductOptionFixture().setId(order.items().get(0)
                    .productOptionId())
                    .setProduct(product)
                    .create()
            );
            /* 집계 데이터 생성 */
            dailyProductSalesService.applySalesDelta(order.id());

            //when
            ResultActions result = mockMvc.perform(
                get("/v1/products/top-selling")
                    .param("limit", "10")
            );

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(product.id()))
                .andExpect(jsonPath("$.data[0].name").value(product.name()))
                .andExpect(jsonPath("$.data[0].price").value(product.price()))
                .andExpect(jsonPath("$.data[0].options").isArray())
                .andExpect(jsonPath("$.data[0].options").isNotEmpty())
                .andExpect(jsonPath("$.data[0].options[0]").isMap())
                .andExpect(jsonPath("$.data[0].options[0].id").value(option.id()))
                .andExpect(jsonPath("$.data[0].options[0].name").value(option.name()))
                .andExpect(jsonPath("$.data[0].options[0].stock").value(option.stock()));
        }
    }
} 