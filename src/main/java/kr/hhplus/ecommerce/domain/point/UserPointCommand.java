package kr.hhplus.ecommerce.domain.point;

public class UserPointCommand {
    public record Charge(
        long userId,
        int amount
    ) {
    }

    public record Use(
        long userId,
        int amount
    ) {
    }
}
