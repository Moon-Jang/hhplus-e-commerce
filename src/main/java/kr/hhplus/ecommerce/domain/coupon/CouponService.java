package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.COUPON_NOT_FOUND;

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

    @Transactional(readOnly = true)
    public CouponVo getCouponDetail(long id) {
        return couponRepository.findById(id)
            .map(CouponVo::from)
            .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));
    }
} 