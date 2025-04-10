package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPointHistory;
import kr.hhplus.ecommerce.domain.point.UserPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {

    @Override
    public UserPointHistory save(UserPointHistory userPointHistory) {
        // TODO: Implement me
        return null;
    }
} 