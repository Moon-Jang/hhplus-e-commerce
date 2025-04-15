package kr.hhplus.ecommerce.domain.point;

import kr.hhplus.ecommerce.common.FixtureReflectionUtils;
import kr.hhplus.ecommerce.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserPointHistoryFixture implements TestFixture<UserPointHistory> {
    private Long id = 1L;
    private long userId = 1L;
    private int amount = 10_000;
    private UserPointHistory.Type type = UserPointHistory.Type.CHARGE;

    @Override
    public UserPointHistory create() {
        UserPointHistory entity = new UserPointHistory();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}
