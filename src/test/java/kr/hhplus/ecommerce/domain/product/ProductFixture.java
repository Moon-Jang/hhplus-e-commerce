package kr.hhplus.ecommerce.domain.product;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProductFixture implements TestFixture<Product> {
    private Long id = 1L;
    private String name = "테스트 상품";
    private String description = "상품 상세 설명입니다.";
    private int price = 10_000;

    @Override
    public Product create() {
        Product entity = new Product();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
} 