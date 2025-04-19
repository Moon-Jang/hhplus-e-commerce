package kr.hhplus.ecommerce.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import kr.hhplus.ecommerce.common.exception.DomainException;
import static kr.hhplus.ecommerce.common.support.DomainStatus.INSUFFICIENT_STOCK;
import static kr.hhplus.ecommerce.common.support.DomainStatus.INVALID_PARAMETER;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "product_options")
@Table(indexes = {
    @Index(name = "idx_product_option_product_id", columnList = "product_id")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int stock;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainException(INVALID_PARAMETER, "차감될 수량은 0이하 일 수 없습니다.");
        }

        if (this.stock - quantity < 0) {
            throw new DomainException(INSUFFICIENT_STOCK);
        }

        this.stock -= quantity;
    }

    public int price() {
        return product.price();
    }
} 