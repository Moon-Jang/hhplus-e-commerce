package kr.hhplus.ecommerce.infrastructure.common;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "shedlock")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ShedLock {
    @Id
    private String name;
    private String lockedBy;
    private LocalDateTime lockUntil;
    private LocalDateTime lockedAt;
}
