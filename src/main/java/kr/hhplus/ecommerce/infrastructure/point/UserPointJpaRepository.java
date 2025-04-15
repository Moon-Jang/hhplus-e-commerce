package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPointJpaRepository extends JpaRepository<UserPoint, Long> {
    Optional<UserPoint> findByUserId(long userId);
}