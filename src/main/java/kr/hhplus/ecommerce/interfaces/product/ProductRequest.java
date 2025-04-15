package kr.hhplus.ecommerce.interfaces.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ProductRequest {
    public record GetTopSelling(
        @NotNull(message = "limit은 필수입니다.")
        @PositiveOrZero(message = "limit은 0보다 커야합니다.")
        Integer limit
    ) {
    }
}
