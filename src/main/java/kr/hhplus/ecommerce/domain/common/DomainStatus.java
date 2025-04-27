package kr.hhplus.ecommerce.domain.common;

import kr.hhplus.ecommerce.common.support.Status;
import kr.hhplus.ecommerce.domain.point.UserPoint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainStatus implements Status {
    INVALID_PARAMETER("잘못된 요청입니다."),

    // user
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),

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
    
    // coupon
    COUPON_NOT_FOUND("쿠폰을 찾을 수 없습니다."),
    COUPON_EXHAUSTED("쿠폰이 소진되었습니다."),
    COUPON_ISSUANCE_NOT_AVAILABLE("쿠폰 발급 가능 시간이 아닙니다."),
    COUPON_QUANTITY_EXHAUSTED("쿠폰 수량이 소진되었습니다."),
    COUPON_ALREADY_ISSUED("이미 발급된 쿠폰입니다."),
    
    // order
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다."),
    ALREADY_COMPLETED_ORDER("이미 완료된 주문입니다."),
    INVALID_ORDER_PRICE("주문 금액이 올바르지 않습니다."),
    ;

    private final String message;

    @Override
    public String message() {
        return message;
    }
}
