package kr.hhplus.ecommerce.controller;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.controller.request.CouponRequest;
import kr.hhplus.ecommerce.controller.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {
    /**
     * 선착순 쿠폰 목록 조회
     */
    @GetMapping
    public ApiResponse<List<CouponResponse.Coupon>> getCoupons() {
        return ApiResponse.success(
            List.of(
                new CouponResponse.Coupon(
                    1L,
                    "신규가입 할인쿠폰",
                    5000,
                    LocalDateTime.now().minusHours(2),
                    LocalDateTime.now().plusHours(2),
                    100,
                    45
                )
            )
        );
    }

    /**
     * 선착순 쿠폰 발급
     */
    @PostMapping("/{couponId}/issue")
    public ApiResponse<CouponResponse.IssuedCoupon> issue(
        @PathVariable long couponId,
        @Valid @RequestBody CouponRequest.Issue request) {
        
        return ApiResponse.success(
            new CouponResponse.IssuedCoupon(
                101L,
                request.userId(),
                couponId,
                "신규가입 할인쿠폰",
                5000,
                LocalDateTime.now().plusDays(7),
                false,
                null,
                LocalDateTime.now()
            )
        );
    }

    /**
     * 보유한 쿠폰 목록 조회
     */
    @GetMapping("/issued/{userId}")
    public ApiResponse<List<CouponResponse.IssuedCoupon>> getUserCoupons(
            @PathVariable long userId,
            @RequestParam(required = false) Boolean used
    ) {
        return ApiResponse.success(
            List.of(
                new CouponResponse.IssuedCoupon(
                    101L,
                    userId,
                    1L,
                    "신규가입 할인쿠폰",
                    5000,
                    LocalDateTime.now().plusDays(7),
                    false,
                    null,
                    LocalDateTime.now().minusDays(1)
                )
            )
        );
    }
} 