package kr.hhplus.ecommerce.interfaces.coupon;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.domain.coupon.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.CouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.interfaces.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;

    @PostMapping("/{couponId}/issue")
    ApiResponse<Void> issueCoupon(@PathVariable long couponId,
                                  @Valid @RequestBody CouponRequest.Issue request) {
        CouponCommand.RequestIssuance command = new CouponCommand.RequestIssuance(
            request.userId(),
            couponId,
            System.currentTimeMillis()
        );
        issuedCouponService.requestIssuance(command);
        
        return ApiResponse.success();
    }
    
    @GetMapping("/available")
    ApiResponse<List<CouponResponse.CouponSummary>> getAvailableCoupons() {
        List<CouponVo> coupons = couponService.getAvailableCoupons();
        
        return ApiResponse.success(
            coupons.stream()
                .map(CouponResponse.CouponSummary::from)
                .toList()
        );
    }

    @GetMapping("/{couponId}")
    ApiResponse<CouponResponse.CouponSummary> getCouponDetail(@PathVariable long couponId) {
        CouponVo coupon = couponService.getCouponDetail(couponId);
        
        return ApiResponse.success(
            CouponResponse.CouponSummary.from(coupon)
        );
    }
} 