package kr.hhplus.ecommerce.interfaces.coupon.api;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponWithDetailsVo;
import kr.hhplus.ecommerce.interfaces.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/issued-coupons")
@RequiredArgsConstructor
public class IssuedCouponController {
    private final IssuedCouponService issuedCouponService;

    @GetMapping
    ApiResponse<List<CouponResponse.IssuedCouponDetails>> getIssuedCoupons(@RequestParam long userId) {
        List<IssuedCouponWithDetailsVo> issuedCoupons = issuedCouponService.getActiveIssuedCoupons(userId);

        return ApiResponse.success(
            issuedCoupons.stream()
                .map(CouponResponse.IssuedCouponDetails::fromWithDetails)
                .toList()
        );
    }
}
