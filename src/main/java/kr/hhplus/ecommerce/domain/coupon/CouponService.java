package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.COUPON_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssuedCouponVo issue(CouponCommand.Issue command) {
        User user = userRepository.findById(command.userId())
                .filter(User::isActive)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));

        IssuedCoupon issuedCoupon = coupon.issue(user.id());

        return IssuedCouponVo.from(
            issuedCouponRepository.save(issuedCoupon)
        );
    }
} 