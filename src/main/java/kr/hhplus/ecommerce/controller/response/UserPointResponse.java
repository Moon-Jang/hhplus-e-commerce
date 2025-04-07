package kr.hhplus.ecommerce.controller.response;

public class UserPointResponse {
    public record UserPoint(
            long id,
            long userId,
            long amount
    ) {
    }
}
