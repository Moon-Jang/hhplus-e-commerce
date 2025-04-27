package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.USER_NOT_FOUND;
import static kr.hhplus.ecommerce.domain.common.DomainStatus.USER_POINT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

    @Transactional
    public UserPointVo charge(UserPointCommand.Charge command) {
        User user = userRepository.findById(command.userId())
            .filter(User::isActive)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        UserPoint userPoint = userPointRepository.findByUserId(user.id())
            .orElseGet(() -> UserPoint.empty(user.id()));
        
        userPoint.charge(command.amount());
        UserPoint savedUserPoint = userPointRepository.save(userPoint);

        UserPointHistory chargeHistory = UserPointHistory.createChargeHistory(user.id(), command.amount());
        userPointHistoryRepository.save(chargeHistory);
        
        return UserPointVo.from(savedUserPoint);
    }

    @Transactional
    public UserPointVo use(UserPointCommand.Use command) {
        User user = userRepository.findById(command.userId())
            .filter(User::isActive)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        UserPoint userPoint = userPointRepository.findByUserId(user.id())
            .orElseThrow(() -> new NotFoundException(USER_POINT_NOT_FOUND));

        userPoint.use(command.amount());
        UserPoint savedUserPoint = userPointRepository.save(userPoint);

        UserPointHistory useHistory = UserPointHistory.createUseHistory(user.id(), command.amount());
        userPointHistoryRepository.save(useHistory);

        return UserPointVo.from(savedUserPoint);
    }
} 