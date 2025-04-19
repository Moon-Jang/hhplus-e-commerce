package kr.hhplus.ecommerce.domain.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.hhplus.ecommerce.common.entity.BaseEntity;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity(name = "order_items")
@Table(indexes = {
    @Index(name = "idx_order_item_product_option_id", columnList = "productOptionId")
})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long productOptionId;
    private int productPrice;
    private int quantity;
    private int amount;

    public OrderItem(long productOptionId,
                     int productPrice,
                     int quantity) {
        this.productOptionId = productOptionId;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.amount = productPrice * quantity;
    }

    public OrderItem(ProductOption productOption,
                     int quantity) {
        this(productOption.id(), productOption.price(), quantity);
    }
}