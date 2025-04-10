package kr.hhplus.ecommerce.domain.user;

import java.time.LocalDateTime;

public record UserVo(
    long id,
    String name,
    LocalDateTime withdrawnAt
) {
    public static UserVo from(User user) {
        return new UserVo(
            user.id(),
            user.name(),
            user.withdrawnAt()
        );
    }
}