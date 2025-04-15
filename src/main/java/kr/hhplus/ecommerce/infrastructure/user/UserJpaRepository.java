package kr.hhplus.ecommerce.infrastructure.user;

import kr.hhplus.ecommerce.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
