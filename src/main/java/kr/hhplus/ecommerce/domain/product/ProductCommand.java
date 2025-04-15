package kr.hhplus.ecommerce.domain.product;

import java.util.List;

public class ProductCommand {
    public record DeductStock(
        List<Item> items
    ) {
        public List<Long> productOptionIds() {
            return items.stream()
                .map(Item::productOptionId)
                .toList();
        }

        public record Item(
            long productOptionId,
            int quantity
        ) {
        }
    }
}
