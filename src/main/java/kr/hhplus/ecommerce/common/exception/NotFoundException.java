package kr.hhplus.ecommerce.common.exception;

import kr.hhplus.ecommerce.common.support.Status;

public class NotFoundException extends ApplicationException {
    public NotFoundException(Status status) {
        super(status);
    }

    public NotFoundException(Status status, String message) {
        super(status, message);
    }

    public NotFoundException(Status status, Throwable cause) {
        super(status, cause);
    }

    public NotFoundException(Status status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
