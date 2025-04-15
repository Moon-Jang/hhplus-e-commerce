package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.domain.order.OrderCommand;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.point.UserPointCommand;
import kr.hhplus.ecommerce.domain.point.UserPointService;
import kr.hhplus.ecommerce.domain.product.ProductCommand;
import kr.hhplus.ecommerce.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final IssuedCouponService issuedCouponService;
    private final ProductService productService;
    private final UserPointService userPointService;

    @Transactional
    public OrderVo process(OrderCommand.Create command) {
        OrderVo order = orderService.create(command);
        productService.deductStock(deductStockCommand(order));
        order.issuedCouponId().ifPresent(issuedCouponService::use);
        userPointService.use(new UserPointCommand.Use(command.userId(), order.finalAmount().intValue()));
        paymentService.pay(new PaymentCommand.Pay(order.id()));
        return orderService.complete(new OrderCommand.Complete(order.id()));
    }

    private ProductCommand.DeductStock deductStockCommand(OrderVo order) {
        List<ProductCommand.DeductStock.Item> items = order.items().stream()
            .map(item -> new ProductCommand.DeductStock.Item(item.productOptionId(), item.quantity()))
            .toList();

        return new ProductCommand.DeductStock(items);
    }
} 