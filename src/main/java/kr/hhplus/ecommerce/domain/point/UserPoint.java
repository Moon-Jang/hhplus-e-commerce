package kr.hhplus.ecommerce.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import kr.hhplus.ecommerce.common.exception.DomainException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static kr.hhplus.ecommerce.common.support.DomainStatus.*;

@Entity(name = "user_points")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserPoint extends BaseEntity {
    public static final int MIN_CHARGE_AMOUNT = 100;
    public static final int MAX_CHARGE_AMOUNT = 1_000_000;
    public static final int MAX_BALANCE = 1_000_000;
    public static final int MIN_USE_AMOUNT = 1;

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private int amount;
    @Version
    private int version;
    
    public UserPoint(Long id, Long userId, int amount) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.version = 1;
    }

    public static UserPoint empty(long userId) {
        return new UserPoint(null, userId, 0);
    }
    
    public void charge(int amount) {
        if (amount < MIN_CHARGE_AMOUNT || amount > MAX_CHARGE_AMOUNT) {
            throw new DomainException(INVALID_CHARGE_AMOUNT);
        }

        if (this.amount + amount > MAX_BALANCE) {
            throw new DomainException(EXCEEDED_MAX_USER_POINT);
        }

        this.amount += amount;
    }

    public void use(int amount) {
        if (amount < MIN_USE_AMOUNT) {
            throw new DomainException(INVALID_USE_AMOUNT);
        }

        if (this.amount < amount) {
            throw new DomainException(INSUFFICIENT_BALANCE);
        }

        this.amount -= amount;
    }
} 