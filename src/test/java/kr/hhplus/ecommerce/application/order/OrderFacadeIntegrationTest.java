package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.domain.coupon.*;
import kr.hhplus.ecommerce.domain.order.OrderCommand;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.point.UserPoint;
import kr.hhplus.ecommerce.domain.point.UserPointFixture;
import kr.hhplus.ecommerce.domain.product.Product;
import kr.hhplus.ecommerce.domain.product.ProductFixture;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionFixture;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.infrastructure.coupon.CouponJpaRepository;
import kr.hhplus.ecommerce.infrastructure.coupon.IssuedCouponJpaRepository;
import kr.hhplus.ecommerce.infrastructure.order.OrderJpaRepository;
import kr.hhplus.ecommerce.infrastructure.point.UserPointJpaRepository;
import kr.hhplus.ecommerce.infrastructure.product.ProductJpaRepository;
import kr.hhplus.ecommerce.infrastructure.product.ProductOptionJpaRepository;
import kr.hhplus.ecommerce.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.common.support.DomainStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OrderFacadeIntegrationTest extends IntegrationTestContext {
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private IssuedCouponService issuedCouponService;
    
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserPointJpaRepository userPointJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private ProductOptionJpaRepository productOptionJpaRepository;
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private IssuedCouponJpaRepository issuedCouponJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;

    private User user;
    private UserPoint userPoint;
    private Product product;
    private ProductOption productOption;
    private Coupon coupon;
    private IssuedCoupon issuedCoupon;
    
    @BeforeEach
    void setUp() {
        user = userJpaRepository.save(new UserFixture().setId(null).create());
        userPoint = userPointJpaRepository.save(new UserPointFixture()
            .setId(null)
            .setUserId(user.id())
            .setAmount(Integer.MAX_VALUE)
            .create());
        product = productJpaRepository.save(new ProductFixture().setId(null).setPrice(10000).create());
        productOption = productOptionJpaRepository.save(new ProductOptionFixture()
            .setId(null)
            .setProduct(product)
            .setStock(100)
            .create());
        coupon = couponJpaRepository.save(new CouponFixture().setId(null).create());
        issuedCoupon = issuedCouponJpaRepository.save(new IssuedCouponFixture()
            .setId(null)
            .setUserId(user.id())
            .setCoupon(coupon)
            .create());
    }
    
    @Nested
    @DisplayName("주문 처리 테스트")
    class ProcessTest {
        @Test
        void 주문_처리_성공_쿠폰_포함() {
            // given
            int quantity = 2;
            int initialStock = productOption.stock();
            int initialPoint = userPoint.amount();
            int totalAmount = productOption.price() * quantity;
            int discountAmount = coupon.discountAmount();
            int expectedFinalAmount = totalAmount - discountAmount;

            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.of(issuedCoupon.id())
            );

            // when
            OrderVo result = orderFacade.process(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).productOptionId()).isEqualTo(productOption.id());
            assertThat(result.items().get(0).quantity()).isEqualTo(quantity);
            assertThat(result.totalAmount().intValue()).isEqualTo(totalAmount);
            assertThat(result.discountAmount().intValue()).isEqualTo(discountAmount);
            assertThat(result.finalAmount().intValue()).isEqualTo(expectedFinalAmount);
            assertThat(result.issuedCouponId()).isPresent();
            assertThat(result.issuedCouponId().get()).isEqualTo(issuedCoupon.id());
            assertThat(result.status()).isEqualTo(kr.hhplus.ecommerce.domain.order.Order.Status.COMPLETED);

            // 재고 확인
            ProductOption updatedOption = productOptionJpaRepository.findById(productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(initialStock - quantity);

            // 포인트 확인
            UserPoint updatedPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(initialPoint - expectedFinalAmount);

            // 쿠폰 사용 확인
            IssuedCoupon updatedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.id()).orElseThrow();
            assertThat(updatedCoupon.isUsed()).isTrue();
        }

        @Test
        void 주문_처리_성공_쿠폰_미포함() {
            // given
            int quantity = 2;
            int initialStock = productOption.stock();
            int initialPoint = userPoint.amount();
            int expectedFinalAmount = productOption.price() * quantity;

            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.empty() // 쿠폰 사용 안함
            );

            // when
            OrderVo result = orderFacade.process(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(user.id());
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).productOptionId()).isEqualTo(productOption.id());
            assertThat(result.items().get(0).quantity()).isEqualTo(quantity);
            assertThat(result.totalAmount().intValue()).isEqualTo(expectedFinalAmount);
            assertThat(result.finalAmount().intValue()).isEqualTo(expectedFinalAmount);
            assertThat(result.status()).isEqualTo(kr.hhplus.ecommerce.domain.order.Order.Status.COMPLETED);

            // 재고 확인
            ProductOption updatedOption = productOptionJpaRepository.findById(productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(initialStock - quantity);

            // 포인트 확인
            UserPoint updatedPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(initialPoint - expectedFinalAmount);
        }

        @Test
        void 재고_부족시_주문_실패() {
            // given
            int quantity = productOption.stock() + 10; // 재고보다 많은 주문 수량
            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.empty()
            );
            int initialStock = productOption.stock();
            
            // when
            Throwable throwable = catchThrowable(() -> orderFacade.process(command));
            
            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INSUFFICIENT_STOCK)
                .hasMessage(INSUFFICIENT_STOCK.message());
            
            // 재고가 변경되지 않았는지 확인
            ProductOption updatedOption = productOptionJpaRepository.findById(productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(initialStock);
            
            // 포인트가 사용되지 않았는지 확인
            UserPoint updatedPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(userPoint.amount());
        }
        
        @Test
        void 포인트_부족시_주문_실패() {
            // given
            userPointJpaRepository.delete(userPoint);
            userPoint = userPointJpaRepository.save(new UserPointFixture()
                .setId(null)
                .setUserId(user.id())
                .setAmount(0)
                .create());
            int initialPoint = userPoint.amount();
            int quantity = 10;
            
            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.of(issuedCoupon.id())
            );
            
            int initialStock = productOption.stock();
            
            // when
            Throwable throwable = catchThrowable(() -> orderFacade.process(command));
            
            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", INSUFFICIENT_BALANCE)
                .hasMessage(INSUFFICIENT_BALANCE.message());
            
            // 재고가 변경되지 않았는지 확인
            ProductOption updatedOption = productOptionJpaRepository.findById(productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(initialStock);

            // 쿠폰이 사용되지 않았는지 확인
            IssuedCoupon updatedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.id()).orElseThrow();
            assertThat(updatedCoupon.isUsed()).isFalse();
            
            // 포인트가 사용되지 않았는지 확인
            UserPoint updatedPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(initialPoint);
        }
        
        @Test
        void 이미_사용된_쿠폰으로_주문_실패() {
            // given
            // 쿠폰 발급 및 사용
            issuedCouponService.use(issuedCoupon.id());
            
            int quantity = 2;
            
            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.of(issuedCoupon.id())
            );
            
            int initialStock = productOption.stock();
            
            // when
            Throwable throwable = catchThrowable(() -> orderFacade.process(command));
            
            // then
            assertThat(throwable)
                .isInstanceOf(DomainException.class)
                .hasFieldOrPropertyWithValue("status", ALREADY_USED_COUPON)
                .hasMessage(ALREADY_USED_COUPON.message());
            
            // 재고가 변경되지 않았는지 확인
            ProductOption updatedOption = productOptionJpaRepository.findById(productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(initialStock);
            
            // 포인트가 사용되지 않았는지 확인
            UserPoint updatedPoint = userPointJpaRepository.findById(userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(userPoint.amount());
        }
    }
} 