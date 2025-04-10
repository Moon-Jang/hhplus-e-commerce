package kr.hhplus.ecommerce.domain.product;

import java.util.List;
import java.util.stream.Collectors;

public record ProductVo(
    long id,
    String name,
    String description,
    int price,
    List<ProductOptionVo> options
) {
    public static ProductVo from(Product product, List<ProductOption> options) {
        return new ProductVo(
            product.id(),
            product.name(),
            product.description(),
            product.price(),
            options.stream()
                .map(ProductOptionVo::from)
                .collect(Collectors.toList())
        );
    }
    
    public record ProductOptionVo(
        long id,
        String name,
        int stock
    ) {
        public static ProductOptionVo from(ProductOption option) {
            return new ProductOptionVo(
                option.id(),
                option.name(),
                option.stock()
            );
        }
    }
} 