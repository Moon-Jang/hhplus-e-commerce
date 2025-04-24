package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.infrastructure.product.ProductJpaRepository;
import kr.hhplus.ecommerce.infrastructure.product.ProductOptionJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.ecommerce.common.support.DomainStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ProductServiceIntegrationTest extends IntegrationTestContext {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private ProductOptionJpaRepository productOptionJpaRepository;

    private Product product;
    private ProductOption productOption;

    @BeforeEach
    void setUp() {
        product = productJpaRepository.save(new ProductFixture().setId(null).create());
        productOption = productOptionJpaRepository.save(
            new ProductOptionFixture()
                .setId(null)
                .setProduct(product)
                .create()
        );
    }

    @Nested
    @DisplayName("상품 옵션 재고 차감 테스트")
    class DeductStockTest {
        @Test
        void 재고_차감_성공() {
            // given
            int deductQuantity = 10;
            int expectedStock = productOption.stock() - deductQuantity;
            
            ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                productOption.id(), deductQuantity
            );
            ProductCommand.DeductStock command = new ProductCommand.DeductStock(
                List.of(item)
            );

            // when
            productService.deductStock(command);

            // then
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(productOption.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(expectedStock);
        }

        @Test
        void 여러_상품_옵션_재고_차감_성공() {
            // given
            ProductOption secondOption = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .create()
            );
            
            int firstDeductQuantity = 10;
            int secondDeductQuantity = 20;
            
            int expectedFirstStock = productOption.stock() - firstDeductQuantity;
            int expectedSecondStock = secondOption.stock() - secondDeductQuantity;
            
            ProductCommand.DeductStock.Item firstItem = new ProductCommand.DeductStock.Item(
                productOption.id(), firstDeductQuantity
            );
            ProductCommand.DeductStock.Item secondItem = new ProductCommand.DeductStock.Item(
                secondOption.id(), secondDeductQuantity
            );
            
            ProductCommand.DeductStock command = new ProductCommand.DeductStock(
                Arrays.asList(firstItem, secondItem)
            );

            // when
            productService.deductStock(command);

            // then
            Optional<ProductOption> updatedFirstOption = productOptionJpaRepository.findById(productOption.id());
            Optional<ProductOption> updatedSecondOption = productOptionJpaRepository.findById(secondOption.id());
            
            assertThat(updatedFirstOption).isPresent();
            assertThat(updatedFirstOption.get().stock()).isEqualTo(expectedFirstStock);
            
            assertThat(updatedSecondOption).isPresent();
            assertThat(updatedSecondOption.get().stock()).isEqualTo(expectedSecondStock);
        }

        @Test
        void 재고가_부족한_경우_실패() {
            // given
            int initialStock = productOption.stock();
            int deductQuantity = initialStock + 1; // 현재 재고보다 많은 수량
            
            ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                productOption.id(), deductQuantity
            );
            ProductCommand.DeductStock command = new ProductCommand.DeductStock(
                List.of(item)
            );

            // when
            Throwable throwable = catchThrowable(() -> productService.deductStock(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INSUFFICIENT_STOCK)
                .hasMessage(INSUFFICIENT_STOCK.message());
                
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(productOption.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(initialStock);
        }

        @Test
        void 차감_수량이_0이하인_경우_실패() {
            // given
            int initialStock = productOption.stock();
            int deductQuantity = 0; // 0인 수량
            
            ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                productOption.id(), deductQuantity
            );
            ProductCommand.DeductStock command = new ProductCommand.DeductStock(
                List.of(item)
            );

            // when
            Throwable throwable = catchThrowable(() -> productService.deductStock(command));

            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INVALID_PARAMETER)
                .hasMessage("차감될 수량은 0이하 일 수 없습니다.");
                
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(productOption.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(initialStock);
        }

        @Test
        void 존재하지_않는_상품_옵션의_경우_실패() {
            // given
            long nonExistentOptionId = 9999L;
            int deductQuantity = 10;
            
            ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                nonExistentOptionId, deductQuantity
            );
            ProductCommand.DeductStock command = new ProductCommand.DeductStock(
                List.of(item)
            );

            // when
            Throwable throwable = catchThrowable(() -> productService.deductStock(command));

            // then
            assertThat(throwable)
                .isInstanceOf(BadRequestException.class)
                .hasFieldOrPropertyWithValue("status", PRODUCT_OPTION_NOT_FOUND)
                .hasMessage(PRODUCT_OPTION_NOT_FOUND.message());
        }
    }
    
    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {
        @Test
        void 요청이_동시에_들어올때_요청_횟수보다_재고가_부족할시_재고_개수만큼_성공하고_나머지는_실패() throws InterruptedException {
            // given
            int initialStock = 20; // 초기 재고 20개
            ProductOption option = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .setStock(initialStock)
                    .create()
            );

            int threadCount = 10;
            int deductQuantity = 5; // 각 요청당 5개씩 차감
            // 총 요청량: 10 * 5 = 50 > 초기 재고 20
            // 따라서 4개 요청만 성공 가능 (20 / 5 = 4)

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, () -> {
                try {
                    ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                        option.id(), deductQuantity
                    );
                    ProductCommand.DeductStock command = new ProductCommand.DeductStock(List.of(item));
                    productService.deductStock(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(option.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(0); // 재고가 모두 소진되어야 함
            assertThat(successCount.get()).isEqualTo(4); // 성공 카운트는 4여야 함 (20 / 5 = 4)
            assertThat(failCount.get()).isEqualTo(6); // 나머지 6개는 실패해야 함
        }

        @Test
        void 요청이_동시에_들어올때_요청_횟수보다_재고가_많으면_모든_요청이_성공한다() throws InterruptedException {
            // given
            ProductOption option = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .setStock(1000)
                    .create()
            );
            
            int threadCount = 10;
            int deductQuantity = 10;
            int expectedFinalStock = option.stock() - (threadCount * deductQuantity);
            
            // when
            runConcurrent(threadCount, () -> {
                ProductCommand.DeductStock.Item item = new ProductCommand.DeductStock.Item(
                    option.id(), deductQuantity
                );
                ProductCommand.DeductStock command = new ProductCommand.DeductStock(List.of(item));
                productService.deductStock(command);
            });
            
            // then
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(option.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(expectedFinalStock);
        }
    }
} 