package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPointHistory;
import kr.hhplus.ecommerce.domain.point.UserPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {
    private final UserPointHistoryJpaRepository userPointHistoryJpaRepository;

    @Override
    public UserPointHistory save(UserPointHistory userPointHistory) {
        return userPointHistoryJpaRepository.save(userPointHistory);
    }
} 