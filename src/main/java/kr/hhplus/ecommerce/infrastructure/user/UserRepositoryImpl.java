package kr.hhplus.ecommerce.infrastructure.user;

import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Override
    public Optional<User> findById(long userId) {
        // TODO: Implement me
        return Optional.empty();
    }
} 