package kr.hhplus.ecommerce.domain.payment;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.point.UserPoint;
import kr.hhplus.ecommerce.domain.point.UserPointHistory;
import kr.hhplus.ecommerce.domain.point.UserPointHistoryRepository;
import kr.hhplus.ecommerce.domain.point.UserPointRepository;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;
import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_POINT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class PointPaymentStrategy implements PaymentStrategy {
    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    @Override
    public boolean isSupported(Payment.Method payMethod) {
        return payMethod == Payment.Method.POINT;
    }

    @Override
    public void process(Payment payment) {
        User user = userRepository.findById(payment.userId())
            .filter(User::isActive)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        UserPoint userPoint = userPointRepository.findByUserId(user.id())
            .orElseThrow(() -> new NotFoundException(USER_POINT_NOT_FOUND));

        userPoint.use(payment.amount());
        userPointRepository.save(userPoint);

        UserPointHistory useHistory = UserPointHistory.createUseHistory(user.id(), payment.amount());
        userPointHistoryRepository.save(useHistory);
    }
}