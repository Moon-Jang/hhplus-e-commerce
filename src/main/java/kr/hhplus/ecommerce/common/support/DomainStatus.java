package kr.hhplus.ecommerce.common.support;

public enum DomainStatus implements Status {
    INVALID_PARAMETER("잘못된 요청입니다."),
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
