package kr.hhplus.ecommerce.domain.common;

import kr.hhplus.ecommerce.common.support.Status;

public class DomainException extends RuntimeException {
    public final Status status;
    public final boolean isCritical;

    public DomainException(Status status) {
        this(status, status.message(), false);
    }

    public DomainException(Status status, String message) {
        this(status, message, false);
    }

    public DomainException(Status status, boolean isCritical) {
        this(status, status.message(), isCritical);
    }

    public DomainException(Status status, Throwable cause) {
        this(status, status.message(), cause, false);
    }

    public DomainException(Status status, Throwable cause, boolean isCritical) {
        this(status, status.message(), cause, isCritical);
    }

    public DomainException(Status status, String message, boolean isCritical) {
        super(message);
        this.status = status;
        this.isCritical = isCritical;
    }
    public DomainException(Status status, String message, Throwable cause, boolean isCritical) {
        super(message, cause);
        this.status = status;
        this.isCritical = isCritical;
    }

    public Status status() {
        return this.status;
    }

    public String message() {
        return super.getMessage();
    }

    public boolean isCritical() {
        return isCritical;
    }
}
