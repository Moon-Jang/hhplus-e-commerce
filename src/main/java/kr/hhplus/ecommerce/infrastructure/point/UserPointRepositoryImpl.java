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
        UserPoint saved = userPointJpaRepository.save(userPoint);
        userPointJpaRepository.flush(); // 쓰기 지연 방지
        return saved;
    }
}