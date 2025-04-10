package kr.hhplus.ecommerce.domain.point;

public record UserPointVo(
    long id,
    long userId,
    int amount
) {
    public static UserPointVo from(UserPoint entity) {
        return new UserPointVo(
            entity.id(),
            entity.userId(),
            entity.amount()
        );
    }
}