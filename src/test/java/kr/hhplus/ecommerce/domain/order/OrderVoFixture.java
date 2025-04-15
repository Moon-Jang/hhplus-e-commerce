package kr.hhplus.ecommerce.domain.order;

import kr.hhplus.ecommerce.common.TestFixture;
import kr.hhplus.ecommerce.domain.common.Money;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Accessors(chain = true)
public class OrderVoFixture implements TestFixture<OrderVo> {
    private Long id = 1L;
    private long userId = 1L;
    private List<OrderVo.Item> items = new ArrayList<>() {{
        add(new OrderVo.Item(1L, 1L, 10_000, 1, 10_000));
    }};
    private Money totalAmount = Money.wons(10_000);
    private Money discountAmount = Money.ZERO;
    private Money finalAmount = Money.wons(10_000);
    private Optional<Long> issuedCouponId = Optional.empty();
    private Order.Status status = Order.Status.COMPLETED;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Override
    public OrderVo create() {
        return new OrderVo(
            id,
            userId,
            items,
            totalAmount,
            discountAmount,
            finalAmount,
            issuedCouponId,
            status,
            createdAt,
            updatedAt
        );
    }
} 