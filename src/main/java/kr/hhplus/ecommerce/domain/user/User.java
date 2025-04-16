package kr.hhplus.ecommerce.domain.user;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "users")
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime withdrawnAt;
    
    public User(String name) {
        this.name = name;
        this.withdrawnAt = null;
    }

    public boolean isActive() {
        return !isWithdrawn();
    }

    public boolean isWithdrawn() {
        return withdrawnAt != null;
    }
} 