package kr.hhplus.ecommerce.interfaces.order;

import kr.hhplus.ecommerce.application.order.OrderFacade;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @PostMapping
    ApiResponse<OrderResponse.OrderResult> createOrder(@RequestBody OrderRequest.Create request) {
        OrderVo result = orderFacade.process(request.toCommand());
        return ApiResponse.success(
            OrderResponse.OrderResult.from(result)
        );
    }
} 