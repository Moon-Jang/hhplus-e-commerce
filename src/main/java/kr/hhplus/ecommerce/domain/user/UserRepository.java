package kr.hhplus.ecommerce.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long userId);
} 