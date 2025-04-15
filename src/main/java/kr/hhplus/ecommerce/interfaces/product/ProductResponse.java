package kr.hhplus.ecommerce.interfaces.product;

import java.util.List;
import java.util.stream.Collectors;

import kr.hhplus.ecommerce.domain.product.ProductVo;

public class ProductResponse {
    public record ProductSummary(
        long id,
        String name,
        int price,
        List<ProductOptionSummary> options
    ) {
        public static ProductSummary from(ProductVo productVo) {
            List<ProductOptionSummary> options = productVo.options().stream()
                .map(ProductOptionSummary::from)
                .collect(Collectors.toList());
                
            return new ProductSummary(
                productVo.id(),
                productVo.name(),
                productVo.price(),
                options
            );
        }
    }

    public record ProductDetails(
        long id,
        String name,
        int price,
        String description,
        List<ProductOptionSummary> options
    ) {
        public static ProductDetails from(ProductVo productVo) {
            List<ProductOptionSummary> options = productVo.options().stream()
                .map(ProductOptionSummary::from)
                .collect(Collectors.toList());
                
            return new ProductDetails(
                productVo.id(),
                productVo.name(),
                productVo.price(),
                productVo.description(),
                options
            );
        }
    }
    
    public record ProductOptionSummary(
        long id,
        String name,
        int stock
    ) {
        public static ProductOptionSummary from(ProductVo.ProductOptionVo optionVo) {
            return new ProductOptionSummary(
                optionVo.id(),
                optionVo.name(),
                optionVo.stock()
            );
        }
    }
} 