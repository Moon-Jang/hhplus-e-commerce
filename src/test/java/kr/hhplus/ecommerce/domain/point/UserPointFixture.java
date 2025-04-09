package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserPointFixture implements TestFixture<UserPoint> {
    private Long id = 1L;
    private long userId = 1L;
    private int amount = 10_000;
    private int version = 1;

    @Override
    public UserPoint create() {
        UserPoint entity = new UserPoint();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}