package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPoint;
import kr.hhplus.ecommerce.domain.point.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {
    private final UserPointJpaRepository userPointJpaRepository;
    
    @Override
    public Optional<UserPoint> findByUserId(long userId) {
        return userPointJpaRepository.findByUserId(userId);
    }
    
    @Override
    public UserPoint save(UserPoint userPoint) {
        return userPointJpaRepository.save(userPoint);
    }
}