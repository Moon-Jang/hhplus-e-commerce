package kr.hhplus.ecommerce.domain.coupon;

import kr.hhplus.ecommerce.common.aspect.DistributedLock;
import kr.hhplus.ecommerce.common.constant.CacheNames;
import kr.hhplus.ecommerce.common.exception.BadRequestException;
import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssuedCouponService {
    private static final int ISSUE_SIZE = 100;
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CouponIssuanceRequestRepository couponIssuanceRequestRepository;
    private final CouponIssuanceRequestPublisher couponIssuanceRequestPublisher;

    @CacheEvict(value = CacheNames.COUPON_DETAILS, key = "#command.couponId")
    @DistributedLock(key = "'ISSUE-COUPON::' + #command.couponId")
    @Transactional
    public IssuedCouponVo issue(CouponCommand.Issue command) {
        if (issuedCouponRepository.isAlreadyIssued(command.couponId(), command.userId())) {
            throw new BadRequestException(COUPON_ALREADY_ISSUED);
        }

        User user = userRepository.findById(command.userId())
                .filter(User::isActive)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));

        IssuedCoupon issuedCoupon = coupon.issue(user.id());
        couponRepository.save(coupon);

        return IssuedCouponVo.from(
            issuedCouponRepository.save(issuedCoupon)
        );
    }

    @Transactional
    public void releaseFromWaitingQueue() {
        List<CouponIssuanceRequest> requests = couponIssuanceRequestRepository.findAllWaitingList(ISSUE_SIZE);

        requests.forEach(request -> {
            try {
                User user = userRepository.findById(request.userId())
                    .filter(User::isActive)
                    .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
                Coupon coupon = couponRepository.findById(request.couponId())
                    .orElseThrow(() -> new NotFoundException(COUPON_NOT_FOUND));

                IssuedCoupon issuedCoupon = coupon.issue(user.id());
                couponRepository.save(coupon);
                issuedCouponRepository.save(issuedCoupon);
            } catch (Exception e) {
                // 중간에 멈추면 안됨으로 예외 발생시 로그 기록
                log.error("Failed to issue coupon for userId: {}, couponId: {}", request.userId(), request.couponId(), e);
            }
        });
    }

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

    public void requestIssuance(CouponCommand.RequestIssuance command) {
        if (issuedCouponRepository.isAlreadyIssued(command.couponId(), command.userId())) {
            throw new BadRequestException(COUPON_ALREADY_ISSUED);
        }

        if (!couponRepository.deductStock(command.couponId())) {
            throw new BadRequestException(COUPON_EXHAUSTED);
        }

        CouponIssuanceRequest request = new CouponIssuanceRequest(
            command.userId(),
            command.couponId(),
            command.requestTimeMillis()
        );
        couponIssuanceRequestPublisher.publish(request);
    }
} 