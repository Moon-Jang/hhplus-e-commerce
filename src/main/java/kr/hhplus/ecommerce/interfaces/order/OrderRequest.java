package kr.hhplus.ecommerce.interfaces.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.ecommerce.domain.order.OrderCommand;

import java.util.List;
import java.util.Optional;

public class OrderRequest {
    public record Create(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotEmpty(message = "주문할 상품은 필수입니다.")
        List<OrderItem> items,

        Optional<Long> issuedCouponId
    ) {
        public OrderCommand.Create toCommand() {
            return new OrderCommand.Create(
                this.userId,
                this.items.stream()
                    .map(item -> new OrderCommand.Create.OrderItem(
                        item.productOptionId,
                        item.quantity
                    ))
                    .toList(),
                issuedCouponId
            );
        }

        public record OrderItem(
            @NotNull(message = "상품 옵션 ID는 필수입니다.")
            Long productOptionId,

            @NotNull(message = "수량은 필수입니다.")
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            Integer quantity
        ) {
        }
    }
} 