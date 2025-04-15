package kr.hhplus.ecommerce.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.application.order.OrderFacade;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<OrderResponse.OrderResult> createOrder(@Valid @RequestBody OrderRequest.Create request) {
        OrderVo result = orderFacade.process(request.toCommand(), request.payMethod());

        return ApiResponse.success(
            OrderResponse.OrderResult.from(result)
        );
    }
} 