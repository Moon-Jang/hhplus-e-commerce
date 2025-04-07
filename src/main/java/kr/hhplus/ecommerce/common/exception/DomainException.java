package kr.hhplus.ecommerce.common.exception;

import kr.hhplus.ecommerce.common.support.Status;

public class DomainException extends ApplicationException {
    private final boolean isCritical;

    public DomainException(Status status) {
        super(status);
        this.isCritical = false;
    }

    public DomainException(Status status, String message) {
        super(status, message);
        this.isCritical = false;
    }

    public DomainException(Status status, Throwable cause) {
        super(status, cause);
        this.isCritical = false;
    }

    public DomainException(Status status, String message, Throwable cause) {
        super(status, message, cause);
        this.isCritical = false;
    }

    public DomainException(Status status, boolean isCritical) {
        super(status);
        this.isCritical = isCritical;
    }

    public DomainException(Status status, String message, boolean isCritical) {
        super(status, message);
        this.isCritical = isCritical;
    }

    public boolean isCritical() {
        return isCritical;
    }
}
