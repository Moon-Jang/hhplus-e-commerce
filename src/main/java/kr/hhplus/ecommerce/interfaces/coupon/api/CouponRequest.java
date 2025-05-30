package kr.hhplus.ecommerce.interfaces.coupon.api;

import jakarta.validation.constraints.NotNull;

public class CouponRequest {
    public record Issue(
        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId
    ) {
    }
} 