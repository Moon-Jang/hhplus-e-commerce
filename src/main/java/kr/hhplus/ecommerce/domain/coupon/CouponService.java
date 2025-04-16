package kr.hhplus.ecommerce.domain.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    
    @Transactional(readOnly = true)
    public List<CouponVo> getAvailableCoupons() {
        return couponRepository.findAvailableCoupons(LocalDateTime.now())
            .stream()
            .map(CouponVo::from)
            .collect(Collectors.toList());
    }
} 