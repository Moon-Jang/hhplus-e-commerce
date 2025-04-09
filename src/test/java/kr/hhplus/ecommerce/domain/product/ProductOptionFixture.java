package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProductOptionFixture implements TestFixture<ProductOption> {
    private Long id = 1L;
    private String name = "테스트 옵션";
    private int stock = 100;
    private Product product = new ProductFixture().create();

    @Override
    public ProductOption create() {
        ProductOption entity = new ProductOption();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}