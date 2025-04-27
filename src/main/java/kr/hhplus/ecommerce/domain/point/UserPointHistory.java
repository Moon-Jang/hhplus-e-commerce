package kr.hhplus.ecommerce.domain.point;

import jakarta.persistence.*;
import kr.hhplus.ecommerce.domain.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "user_point_histories")
@Table(indexes = {
    @Index(name = "idx_user_point_history_user_id", columnList = "userId")
})
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

    public static UserPointHistory createUseHistory(long userId, int amount) {
        return new UserPointHistory(userId, amount, Type.USE);
    }

    public enum Type {
        CHARGE, USE
    }
} 