package kr.hhplus.ecommerce.common.support;

import kr.hhplus.ecommerce.domain.point.UserPoint;

public enum DomainStatus implements Status {
    INVALID_PARAMETER("잘못된 요청입니다."),

    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),

    // userPoint
    USER_POINT_NOT_FOUND("사용자 포인트를 찾을 수 없습니다."),
    INVALID_CHARGE_AMOUNT("충전 금액은 %d원 이상 %d원 이하여야 합니다.".formatted(UserPoint.MIN_CHARGE_AMOUNT, UserPoint.MAX_CHARGE_AMOUNT)),
    EXCEEDED_MAX_USER_POINT("충전 후 잔액은 %d원을 초과할 수 없습니다.".formatted(UserPoint.MAX_BALANCE)),
    ;

    private final String message;

    DomainStatus(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
