package kr.hhplus.ecommerce.infrastructure.point;

import kr.hhplus.ecommerce.domain.point.UserPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPointHistoryJpaRepository extends JpaRepository<UserPointHistory, Long> {
    List<UserPointHistory> findAllByUserId(long userId);
}
