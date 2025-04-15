package kr.hhplus.ecommerce.interfaces.point;

import kr.hhplus.ecommerce.domain.point.UserPointVo;

public class UserPointResponse {
    public record UserPoint(
        long id,
        long userId,
        int amount
    ) {
        public static UserPoint from(UserPointVo userPointVo) {
            return new UserPoint(
                userPointVo.id(),
                userPointVo.userId(),
                userPointVo.amount()
            );
        }
    }
} 