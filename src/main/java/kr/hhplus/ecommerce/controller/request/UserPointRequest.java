package kr.hhplus.ecommerce.controller.request;

import jakarta.validation.constraints.NotNull;

public class UserPointRequest {
    public record Charge(
        @NotNull(message = "포인트 충전 금액은 필수입니다.")
        Long amount
    ) {
    }
}
