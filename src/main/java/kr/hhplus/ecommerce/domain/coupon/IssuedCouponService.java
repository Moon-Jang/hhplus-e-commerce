package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.ISSUED_COUPON_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {
    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public void use(long id) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(id)
                .orElseThrow(() -> new DomainException(ISSUED_COUPON_NOT_FOUND));
        
        issuedCoupon.use();
        issuedCouponRepository.save(issuedCoupon);
    }
    
    @Transactional(readOnly = true)
    public IssuedCoupon findById(long id) {
        return issuedCouponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ISSUED_COUPON_NOT_FOUND));
    }
} 