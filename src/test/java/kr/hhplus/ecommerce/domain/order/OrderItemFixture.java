package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OrderItemFixture implements TestFixture<OrderItem> {
    private Long id = 1L;
    private long productOptionId = 1L;
    private int productPrice = 10_000;
    private int quantity = 1;
    private int amount = 10_000;

    @Override
    public OrderItem create() {
        OrderItem entity = new OrderItem();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
} 