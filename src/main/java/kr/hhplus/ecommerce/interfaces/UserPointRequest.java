package kr.hhplus.ecommerce.interfaces;

import jakarta.validation.constraints.NotNull;

public class UserPointRequest {
    public record Charge(
        @NotNull(message = "userId는 필수입니다.")
        Long userId,
        @NotNull(message = "충전 금액은 필수입니다.")
        Integer amount
    ) {
    }
} 