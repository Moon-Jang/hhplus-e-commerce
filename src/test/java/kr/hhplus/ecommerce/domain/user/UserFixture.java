package kr.hhplus.ecommerce.domain.user;

import java.time.LocalDateTime;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserFixture implements TestFixture<User> {
    private Long id = 1L;
    private String name = "test";
    private LocalDateTime withdrawnAt = null;

    @Override
    public User create() {
        User entity = new User();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}