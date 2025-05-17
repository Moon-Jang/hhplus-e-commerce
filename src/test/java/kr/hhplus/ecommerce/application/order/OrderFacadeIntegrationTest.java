package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.domain.common.DomainException;
import kr.hhplus.ecommerce.domain.coupon.*;
import kr.hhplus.ecommerce.domain.order.OrderCommand;
import kr.hhplus.ecommerce.domain.order.OrderVo;
import kr.hhplus.ecommerce.domain.point.UserPoint;
import kr.hhplus.ecommerce.domain.point.UserPointCommand;
import kr.hhplus.ecommerce.domain.point.UserPointFixture;
import kr.hhplus.ecommerce.domain.point.UserPointService;
import kr.hhplus.ecommerce.domain.product.Product;
import kr.hhplus.ecommerce.domain.product.ProductFixture;
import kr.hhplus.ecommerce.domain.product.ProductOption;
import kr.hhplus.ecommerce.domain.product.ProductOptionFixture;
import kr.hhplus.ecommerce.domain.user.User;
import kr.hhplus.ecommerce.domain.user.UserFixture;
import kr.hhplus.ecommerce.infrastructure.order.OrderJpaRepository;
import kr.hhplus.ecommerce.infrastructure.point.UserPointJpaRepository;
import kr.hhplus.ecommerce.infrastructure.product.ProductJpaRepository;
import kr.hhplus.ecommerce.infrastructure.product.ProductOptionJpaRepository;
import kr.hhplus.ecommerce.infrastructure.user.UserJpaRepository;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.ecommerce.domain.common.DomainStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class OrderFacadeIntegrationTest extends IntegrationTestContext {
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private IssuedCouponService issuedCouponService;
    @Autowired
    private UserPointService userPointService;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private UserPointJpaRepository userPointJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private ProductOptionJpaRepository productOptionJpaRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
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
        coupon = couponRepository.save(new CouponFixture().setId(null).create());
        issuedCoupon = issuedCouponRepository.save(new IssuedCouponFixture()
            .setId(null)
            .setUserId(user.id())
            .setCouponId(coupon.id())
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
            IssuedCoupon updatedCoupon = issuedCouponRepository.findById(issuedCoupon.id()).orElseThrow();
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
            IssuedCoupon updatedCoupon = issuedCouponRepository.findById(issuedCoupon.id()).orElseThrow();
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

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {
        @Test
        void 동일한_유저의_요청이_동시에_들어오면_순차적으로_처리된다() throws InterruptedException {
            // given
            SetUpData setUpData = new SetUpData().setUp();
            int requestQuantity = 1;
            OrderCommand.Create command = new OrderCommand.Create(
                setUpData.user.id(),
                List.of(new OrderCommand.Create.OrderItem(setUpData.productOption.id(), requestQuantity)),
                Optional.empty()
            );
            int threadCount = 3;
            int initialStock = setUpData.productOption.stock();
            int initialPoint = setUpData.userPoint.amount();
            int expectedStock = initialStock - (threadCount * requestQuantity);
            int expectedPoint = initialPoint - (threadCount * setUpData.productOption.price() * requestQuantity);

            // when
            runConcurrent(threadCount, () ->  orderFacade.process(command));

            // then
            assertThat(orderJpaRepository.count()).isEqualTo(threadCount);

            ProductOption updatedOption = productOptionJpaRepository.findById(setUpData.productOption.id()).orElseThrow();
            assertThat(updatedOption.stock()).isEqualTo(expectedStock);

            UserPoint updatedPoint = userPointJpaRepository.findById(setUpData.userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(expectedPoint);
        }

        @Test
        void 주문_결제와_포인트_충전이_동시에_발생해도_포인트_데이터의_정합성이_유지된다() throws InterruptedException {
            // given
            SetUpData setUpData = new SetUpData()

                .setUp();
            int requestQuantity = 1;
            OrderCommand.Create orderCommand = new OrderCommand.Create(
                setUpData.user.id(),
                List.of(new OrderCommand.Create.OrderItem(setUpData.productOption.id(), requestQuantity)),
                Optional.empty()
            );
            UserPointCommand.Charge chargeCommand = new UserPointCommand.Charge(
                setUpData.user.id(),
                100
            );
            int initialPoint = setUpData.userPoint.amount();
            int expectedPoint = initialPoint - (setUpData.productOption.price() * requestQuantity) + chargeCommand.amount();

            // when
            runConcurrent(new Runnable[]{
                () -> orderFacade.process(orderCommand),
                () -> userPointService.charge(chargeCommand)
            });

            // then
            assertThat(orderJpaRepository.count()).isEqualTo(1);

            UserPoint updatedPoint = userPointJpaRepository.findById(setUpData.userPoint.id()).orElseThrow();
            assertThat(updatedPoint.amount()).isEqualTo(expectedPoint);
        }
    }

    @Setter
    @Accessors(chain = true)
    private class SetUpData {
        public User user;
        public UserPoint userPoint;
        public Product product;
        public ProductOption productOption;
        public Coupon coupon;
        public IssuedCoupon issuedCoupon;
        public UserFixture userFixture = new UserFixture();
        public UserPointFixture userPointFixture = new UserPointFixture();
        public ProductFixture productFixture = new ProductFixture();
        public ProductOptionFixture productOptionFixture = new ProductOptionFixture();
        public CouponFixture couponFixture;
        public IssuedCouponFixture issuedCouponFixture;

        public SetUpData setUp() {
            user = userJpaRepository.save(userFixture.setId(null).create());
            userPoint = userPointJpaRepository.save(userPointFixture
                .setId(null)
                .setUserId(user.id())
                .setAmount(UserPoint.MAX_BALANCE - 100_000)
                .create());
            product = productJpaRepository.save(productFixture.setId(null).setPrice(10000).create());
            productOption = productOptionJpaRepository.save(productOptionFixture
                .setId(null)
                .setProduct(product)
                .setStock(100)
                .create());

            if (couponFixture != null) {
                coupon = couponRepository.save(couponFixture.setId(null).create());
            }

            if (issuedCouponFixture != null) {
                issuedCoupon = issuedCouponRepository.save(issuedCouponFixture
                    .setId(null)
                    .setUserId(user.id())
                    .setCouponId(coupon.id())
                    .create());
            }
            return this;
        }
    }
} 