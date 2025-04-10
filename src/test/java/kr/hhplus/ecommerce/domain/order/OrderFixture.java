package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class OrderFixture implements TestFixture<Order> {
    private Long id = 1L;
    private long userId = 1L;
    private Long issuedCouponId = null;
    private int totalAmount = 10_000;
    private int discountAmount = 0;
    private int finalAmount = 10_000;
    private Order.Status status = Order.Status.PENDING;
    private List<OrderItem> items = new ArrayList<>(){{
        add(new OrderItemFixture().create());
    }};

    @Override
    public Order create() {
        Order entity = new Order();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
} 