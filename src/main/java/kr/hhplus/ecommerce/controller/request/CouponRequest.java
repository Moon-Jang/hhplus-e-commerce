package kr.hhplus.ecommerce.controller.request;

import jakarta.validation.constraints.NotNull;

public class CouponRequest {
    public record Issue(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId
    ) {
    }
} 