package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPoint;
import kr.hhplus.ecommerce.domain.point.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {
    
    @Override
    public Optional<UserPoint> findByUserId(long userId) {
        // TODO: Implement me
        return Optional.empty();
    }
    
    @Override
    public UserPoint save(UserPoint userPoint) {
        // TODO: Implement me
        return null;
    }
}