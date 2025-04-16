package kr.hhplus.ecommerce.interfaces.coupon;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.domain.coupon.*;
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
    ApiResponse<CouponResponse.IssuedCouponDetails> issueCoupon(@PathVariable long couponId,
                                                                @Valid @RequestBody CouponRequest.Issue request) {
        CouponCommand.Issue command = new CouponCommand.Issue(request.userId(), couponId);
        IssuedCouponVo issuedCoupon = issuedCouponService.issue(command);
        
        return ApiResponse.success(
            CouponResponse.IssuedCouponDetails.from(issuedCoupon)
        );
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
} 