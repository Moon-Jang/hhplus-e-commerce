package kr.hhplus.ecommerce.controller.response;

public class ProductResponse {
    public record ProductSummary(
            long id,
            String name,
            long price,
            int quantity
    ) {
    }

    public record ProductDetail(
            long id,
            String name,
            long price,
            int quantity,
            String description
    ) {
    }

    public record PopularProduct(
            long id,
            String name,
            long price,
            int quantity,
            int soldCount
    ) {
    }
} 