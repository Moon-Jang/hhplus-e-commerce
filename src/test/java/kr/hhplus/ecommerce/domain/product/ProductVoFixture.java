package kr.hhplus.ecommerce.domain.product;


import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.domain.product.ProductVo.ProductOptionVo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ProductVoFixture implements TestFixture<ProductVo> {
    private Long id = 1L;
    private String name = "테스트 상품";
    private String description = "상품 상세 설명입니다.";
    private int price = 10_000;
    private List<ProductOptionVo> options = new ArrayList<>(){{
        add(new ProductOptionVo(1L, "옵션 1", 10));
        add(new ProductOptionVo(2L, "옵션 2", 20));
    }};

    @Override
    public ProductVo create() {
        return new ProductVo(
            id,
            name,
            description,
            price,
            options
        );
    }
}
