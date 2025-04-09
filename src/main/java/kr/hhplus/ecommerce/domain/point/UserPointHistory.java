package kr.hhplus.ecommerce.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "user_point_histories")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserPointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long userId;
    private int amount;
    private Type type;

    public UserPointHistory(long userId, int amount, Type type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public static UserPointHistory createChargeHistory(long userId, int amount) {
        return new UserPointHistory(userId, amount, Type.CHARGE);
    }

    public enum Type {
        CHARGE, USE
    }
} 