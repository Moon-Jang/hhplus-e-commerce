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
    INVALID_USE_AMOUNT("사용 금액은 %d원 이상이어야 합니다.".formatted(UserPoint.MIN_USE_AMOUNT)),
    INSUFFICIENT_BALANCE("잔액이 부족합니다."),

    // product
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND("상품 옵션을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK("재고가 부족합니다."),

    // issued-coupon
    ISSUED_COUPON_NOT_FOUND("발급된 쿠폰을 찾을 수 없습니다."),
    EXPIRED_COUPON("만료된 쿠폰입니다."),
    ALREADY_USED_COUPON("이미 사용된 쿠폰입니다."),
    
    // order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ALREADY_COMPLETED_ORDER("이미 완료된 주문입니다."),

    // payment
    PAYMENT_METHOD_NOT_SUPPORTED("지원하지 않는 결제 수단입니다."),
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
