package kr.hhplus.ecommerce.interfaces.coupon;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.domain.coupon.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.CouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping("/{couponId}/issue")
    ApiResponse<CouponResponse.IssuedCouponDetails> issueCoupon(@PathVariable long couponId,
                                                                @Valid @RequestBody CouponRequest.Issue request) {
        CouponCommand.Issue command = new CouponCommand.Issue(request.userId(), couponId);
        IssuedCouponVo issuedCoupon = couponService.issue(command);
        
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