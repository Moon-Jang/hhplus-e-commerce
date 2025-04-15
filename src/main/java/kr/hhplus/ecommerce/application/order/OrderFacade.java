package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.domain.order.OrderCommand;
import kr.hhplus.ecommerce.domain.order.OrderService;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.payment.Payment;
import kr.hhplus.ecommerce.domain.payment.PaymentCommand;
import kr.hhplus.ecommerce.domain.payment.PaymentService;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.user.UserService;
import kr.hhplus.ecommerce.domain.user.UserVo;
import kr.hhplus.ecommerce.interfaces.order.OrderApplicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final IssuedCouponService issuedCouponService;
    private final ProductService productService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderVo process(OrderCommand.Create command, Payment.Method payMethod) {
        UserVo user = userService.findActiveUserById(command.userId());
        OrderVo order = orderService.create(command);
        command.items()
            .forEach(item -> productService.decreaseStock(item.productOptionId(), item.quantity()));
        command.issuedCouponId()
            .ifPresent(issuedCouponService::use);

        try {
            paymentService.pay(new PaymentCommand.Pay(order.id(), payMethod));
        } catch (Exception e) {
            eventPublisher.publishEvent(new OrderApplicationEvent.PaymentFailure(order, payMethod, e.getMessage()));
            throw e;
        }

        OrderVo completedOrder = orderService.complete(new OrderCommand.Complete(order.id()));
        eventPublisher.publishEvent(new OrderApplicationEvent.Complete(completedOrder));

        return completedOrder;
    }
} 