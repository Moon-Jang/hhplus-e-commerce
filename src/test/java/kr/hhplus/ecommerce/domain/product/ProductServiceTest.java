package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;

    @Nested
    @DisplayName("상품 목록 조회 테스트")
    class FindAllTest {
        @Test
        void 성공() {
            // given
            List<Product> products = List.of(
                new ProductFixture().setId(1L).create(),
                new ProductFixture().setId(2L).create()
            );
            List<ProductOption> productOptions = List.of(
                new ProductOptionFixture().setId(1L).setProduct(products.get(0)).create(),
                new ProductOptionFixture().setId(2L).setProduct(products.get(1)).create()
            );
            List<Long> productIds = products.stream().map(Product::id).toList();
            given(productRepository.findAll()).willReturn(products);
            given(productOptionRepository.findAllByProductIds(productIds)).willReturn(productOptions);

            // when
            List<ProductVo> result = service.findAll();

            // then
            verify(productRepository).findAll();
            verify(productOptionRepository).findAllByProductIds(productIds);
            assertThat(result).hasSize(products.size());
            assertThat(result.get(0).id()).isEqualTo(products.get(0).id());
            assertThat(result.get(0).name()).isEqualTo(products.get(0).name());
            assertThat(result.get(0).description()).isEqualTo(products.get(0).description());
            assertThat(result.get(0).price()).isEqualTo(products.get(0).price());
            assertThat(result.get(0).options().get(0).id()).isEqualTo(productOptions.get(0).id());
            assertThat(result.get(0).options().get(0).name()).isEqualTo(productOptions.get(0).name());
            assertThat(result.get(0).options().get(0).stock()).isEqualTo(productOptions.get(0).stock());
            assertThat(result.get(1).id()).isEqualTo(products.get(1).id());
            assertThat(result.get(1).name()).isEqualTo(products.get(1).name());
            assertThat(result.get(1).description()).isEqualTo(products.get(1).description());
            assertThat(result.get(1).price()).isEqualTo(products.get(1).price());
            assertThat(result.get(1).options().get(0).id()).isEqualTo(productOptions.get(1).id());
            assertThat(result.get(1).options().get(0).name()).isEqualTo(productOptions.get(1).name());
            assertThat(result.get(1).options().get(0).stock()).isEqualTo(productOptions.get(1).stock());
        }
    }

    @Nested
    @DisplayName("상품 목록 조회 By Id 테스트")
    class FindAllByIdTest {
        @Test
        void 성공() {
            // given
            List<Product> products = List.of(
                new ProductFixture().setId(1L).create(),
                new ProductFixture().setId(2L).create()
            );
            List<ProductOption> productOptions = List.of(
                new ProductOptionFixture().setId(1L).setProduct(products.get(0)).create(),
                new ProductOptionFixture().setId(2L).setProduct(products.get(1)).create()
            );
            List<Long> productIds = products.stream().map(Product::id).toList();
            given(productRepository.findAllById(productIds)).willReturn(products);
            given(productOptionRepository.findAllByProductIds(productIds)).willReturn(productOptions);

            // when
            List<ProductVo> result = service.findAllById(productIds);

            // then
            verify(productRepository).findAllById(productIds);
            verify(productOptionRepository).findAllByProductIds(productIds);
            assertThat(result).hasSize(products.size());
            assertThat(result.get(0).id()).isEqualTo(products.get(0).id());
            assertThat(result.get(0).name()).isEqualTo(products.get(0).name());
            assertThat(result.get(0).description()).isEqualTo(products.get(0).description());
            assertThat(result.get(0).price()).isEqualTo(products.get(0).price());
            assertThat(result.get(1).id()).isEqualTo(products.get(1).id());
            assertThat(result.get(1).name()).isEqualTo(products.get(1).name());
            assertThat(result.get(1).description()).isEqualTo(products.get(1).description());
            assertThat(result.get(1).price()).isEqualTo(products.get(1).price());
        }
    }

    @Nested
    @DisplayName("상품 상세 조회 테스트")
    class FindByIdTest {
        @Test
        void 상품이_존재하는_경우_성공() {
            // given
            Product product = new ProductFixture().create();
            List<ProductOption> productOptions = List.of(
                new ProductOptionFixture().setId(1L).setProduct(product).create(),
                new ProductOptionFixture().setId(2L).setProduct(product).create()
            );
            given(productRepository.findById(product.id())).willReturn(Optional.of(product));
            given(productOptionRepository.findAllByProductId(product.id())).willReturn(productOptions);

            // when
            ProductVo result = service.findById(product.id());

            // then
            verify(productRepository).findById(product.id());
            verify(productOptionRepository).findAllByProductId(product.id());
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(product.id());
            assertThat(result.name()).isEqualTo(product.name());
            assertThat(result.description()).isEqualTo(product.description());
            assertThat(result.price()).isEqualTo(product.price());
            assertThat(result.options().get(0).id()).isEqualTo(productOptions.get(0).id());
            assertThat(result.options().get(0).name()).isEqualTo(productOptions.get(0).name());
            assertThat(result.options().get(0).stock()).isEqualTo(productOptions.get(0).stock());
            assertThat(result.options().get(1).id()).isEqualTo(productOptions.get(1).id());
            assertThat(result.options().get(1).name()).isEqualTo(productOptions.get(1).name());
            assertThat(result.options().get(1).stock()).isEqualTo(productOptions.get(1).stock());
        }

        @Test
        void 상품이_존재하지_않는_경우_실패() {
            // given
            long notExistProductId = 999L;
            given(productRepository.findById(notExistProductId)).willReturn(Optional.empty());

            // when
            Throwable throwable = catchThrowable(() -> service.findById(notExistProductId));

            // then
            assertThat(throwable).isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("status", PRODUCT_NOT_FOUND)
                .hasMessage(PRODUCT_NOT_FOUND.message());
        }
    }
} 