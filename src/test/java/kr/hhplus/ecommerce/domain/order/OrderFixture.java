package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class OrderFixture implements TestFixture<Order> {
    private Long id = 1L;
    private long userId = 1L;
    private Long issuedCouponId = null;
    private OrderPriceDetails priceDetails = new OrderPriceFixture().create();
    private Order.Status status = Order.Status.COMPLETED;
    private List<OrderItem> items = new ArrayList<>(){{
        add(new OrderItemFixture().create());
    }};
    private LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

    @Override
    public Order create() {
        Order entity = new Order();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public static OrderFixture newEntity() {
        OrderFixture orderFixture = new OrderFixture().setId(null);
        orderFixture.setItems(
            new ArrayList<>(){{
                add(new OrderItemFixture().setId(null).create());
            }}
        );

        return orderFixture;
    }
} 