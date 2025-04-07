package kr.hhplus.ecommerce.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    public record OrderItem(
            long productId,
            String productName,
            long price,
            int quantity,
            long amount
    ) {
    }
    
    public record UsedCoupon(
            long couponId,
            String name,
            long discountAmount
    ) {
    }
    
    public record Order(
            long orderId,
            long userId,
            List<OrderItem> items,
            long totalAmount,
            long discountAmount,
            long finalAmount,
            UsedCoupon coupon,
            String orderStatus,
            LocalDateTime createdAt
    ) {
    }
} 