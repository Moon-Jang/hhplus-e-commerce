package kr.hhplus.ecommerce.domain.point;

import java.util.Optional;

public interface UserPointRepository {
    Optional<UserPoint> findByUserId(long userId);
    UserPoint save(UserPoint userPoint);
} 