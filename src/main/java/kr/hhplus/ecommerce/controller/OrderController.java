package kr.hhplus.ecommerce.controller;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.controller.request.OrderRequest;
import kr.hhplus.ecommerce.controller.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    /**
     * 주문 생성
     */
    @PostMapping
    public ApiResponse<OrderResponse.Order> create(
        @Valid @RequestBody OrderRequest.Create request
    ) {
        return ApiResponse.success(mockResponse());
    }

    /**
     * 주문 결제
     */
    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderResponse.Order> pay(@PathVariable long orderId) {
        return ApiResponse.success(mockResponse());
    }

    private OrderResponse.Order mockResponse() {
        return new OrderResponse.Order(
            1001L,
            1L,
            List.of(
                new OrderResponse.OrderItem(
                    1L,
                    "스마트폰",
                    1000000,
                    1,
                    1000000
                ),
                new OrderResponse.OrderItem(
                    2L,
                    "노트북",
                    1500000,
                    1,
                    1500000
                )
            ),
            2500000,
            5000,
            2495000,
            new OrderResponse.UsedCoupon(
                1L,
                "신규가입 할인쿠폰",
                5000
            ),
            "COMPLETED",
            LocalDateTime.now()
        );
    }
} 