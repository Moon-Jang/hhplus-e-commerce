package kr.hhplus.ecommerce.domain.user;

import kr.hhplus.ecommerce.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserVo findActiveUserById(long userId) {
        return userRepository.findById(userId)
            .filter(User::isActive)
            .map(UserVo::from)
            .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<UserVo> findById(long userId) {
        return userRepository.findById(userId)
            .map(UserVo::from);
    }
}