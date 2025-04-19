package kr.hhplus.ecommerce.application.order;

import kr.hhplus.ecommerce.common.IntegrationTestContext;
import kr.hhplus.ecommerce.common.exception.DomainException;
import kr.hhplus.ecommerce.domain.coupon.*;
import kr.hhplus.ecommerce.domain.order.Order;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
            CouponCommand.Issue couponCommand = new CouponCommand.Issue(user.id(), coupon.id());
            IssuedCouponVo issuedCouponVo = issuedCouponService.issue(couponCommand);
            issuedCouponService.use(issuedCouponVo.id());
            
            int quantity = 2;
            
            OrderCommand.Create command = new OrderCommand.Create(
                user.id(),
                List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantity)),
                Optional.of(issuedCouponVo.id())
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
        void 제한된_재고의_상품을_여러_사용자가_동시에_주문하면_일부만_성공한다() throws InterruptedException {
            // given
            int initialStock = 5; // 초기 재고
            Product product = productJpaRepository.save(new ProductFixture().setId(null).setPrice(5000).create());
            ProductOption productOption = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .setStock(initialStock)
                    .create()
            );

            // 10명의 사용자 생성
            User[] users = new User[10];
            for (int i = 0; i < users.length; i++) {
                users[i] = userJpaRepository.save(new UserFixture().setId(null).create());

                // 각 사용자에게 충분한 포인트 지급
                userPointJpaRepository.save(
                    new UserPointFixture()
                        .setId(null)
                        .setUserId(users[i].id())
                        .setAmount(50000)
                        .create()
                );
            }

            int threadCount = 10;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, (index) -> {
                try {
                    OrderCommand.Create command = new OrderCommand.Create(
                        users[index].id(),
                        List.of(new OrderCommand.Create.OrderItem(productOption.id(), 1)),
                        Optional.empty() // 쿠폰 미사용
                    );

                    orderFacade.process(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(productOption.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(0); // 모든 재고가 소진됨

            assertThat(successCount.get()).isEqualTo(initialStock); // 성공 개수는 초기 재고와 같아야 함
            assertThat(failCount.get()).isEqualTo(threadCount - initialStock); // 나머지는 실패

            // 성공한 주문 확인
            List<Order> orders = orderJpaRepository.findAll();
            assertThat(orders).hasSize(initialStock);
        }

        @Test
        void 제한된_포인트로_여러_주문을_동시에_시도하면_일부만_성공한다() throws InterruptedException {
            // given
            int initialPoint = 30000; // 초기 포인트
            int orderPrice = 10000; // 각 주문 가격

            // 사용자 생성 및 포인트 설정
            User user = userJpaRepository.save(new UserFixture().setId(null).create());
            UserPoint userPoint = userPointJpaRepository.save(
                new UserPointFixture()
                    .setId(null)
                    .setUserId(user.id())
                    .setAmount(initialPoint)
                    .create()
            );

            // 상품 생성 (충분한 재고)
            Product product = productJpaRepository.save(new ProductFixture().setId(null).setPrice(orderPrice).create());
            ProductOption productOption = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .setStock(100) // 충분한 재고
                    .create()
            );

            int threadCount = 5; // 5개 주문 시도 (총 50,000 포인트 필요 > 초기 30,000)
            int expectedSuccessCount = initialPoint / orderPrice;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, () -> {
                try {
                    OrderCommand.Create command = new OrderCommand.Create(
                        user.id(),
                        List.of(new OrderCommand.Create.OrderItem(productOption.id(), 1)),
                        Optional.empty() // 쿠폰 미사용
                    );

                    orderFacade.process(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            // 포인트 잔액 확인
            Optional<UserPoint> updatedPoint = userPointJpaRepository.findById(userPoint.id());
            assertThat(updatedPoint).isPresent();
            assertThat(updatedPoint.get().amount()).isEqualTo(0); // 모든 포인트 소진

            // 주문 수 확인
            List<Order> orders = orderJpaRepository.findAll();
            assertThat(orders).hasSize(expectedSuccessCount);

            assertThat(successCount.get()).isEqualTo(expectedSuccessCount);
            assertThat(failCount.get()).isEqualTo(threadCount - expectedSuccessCount);
        }

        @Test
        void 사용자_1명이_동일한_쿠폰으로_동시에_여러번_주문하면_일부만_성공한다() throws InterruptedException {
            // given
            int initialPoint = 30000; // 초기 포인트
            int productPrice = 10000; // 각 주문 가격
            int discountAmount = coupon.discountAmount(); // 쿠폰 할인 금액
            int finalAmount = productPrice - discountAmount; // 최종 결제 금액
            int expectedUserPoint = initialPoint - finalAmount;

            // 사용자 생성 및 포인트 설정
            User user = userJpaRepository.save(new UserFixture().setId(null).create());
            UserPoint userPoint = userPointJpaRepository.save(
                new UserPointFixture()
                    .setId(null)
                    .setUserId(user.id())
                    .setAmount(initialPoint)
                    .create()
            );

            int threadCount = 5; // 5개 주문 시도 (총 50,000 포인트 필요 > 초기 30,000)
            int expectedSuccessCount = 1;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);

            // when
            runConcurrent(threadCount, () -> {
                try {
                    OrderCommand.Create command = new OrderCommand.Create(
                        user.id(),
                        List.of(new OrderCommand.Create.OrderItem(productOption.id(), 1)),
                        Optional.of(issuedCoupon.id()) // 쿠폰 사용
                    );

                    orderFacade.process(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            // then
            assertThat(successCount.get()).isEqualTo(expectedSuccessCount);
            assertThat(failCount.get()).isEqualTo(threadCount - expectedSuccessCount);

            // 포인트 잔액 확인
            Optional<UserPoint> updatedPoint = userPointJpaRepository.findById(userPoint.id());
            assertThat(updatedPoint).isPresent();
            assertThat(updatedPoint.get().amount()).isEqualTo(expectedUserPoint);

            // 주문 수 확인
            List<Order> orders = orderJpaRepository.findAll();
            assertThat(orders).hasSize(expectedSuccessCount);

            // 쿠폰 사용 확인
            IssuedCoupon updatedCoupon = issuedCouponJpaRepository.findById(issuedCoupon.id()).orElseThrow();
            assertThat(updatedCoupon.isUsed()).isTrue();
        }

        @Test
        void 여러_사용자가_동시에_다양한_주문을_처리할_때_모든_리소스가_정확히_관리된다() throws InterruptedException {
            // given
            int userCount = 5;
            User[] users = new User[userCount];
            UserPoint[] userPoints = new UserPoint[userCount];

            // 상품 생성
            Product product = productJpaRepository.save(new ProductFixture().setId(null).setPrice(5000).create());
            ProductOption productOption = productOptionJpaRepository.save(
                new ProductOptionFixture()
                    .setId(null)
                    .setProduct(product)
                    .setStock(100)
                    .create()
            );

            // 사용자 및 포인트 설정
            for (int i = 0; i < userCount; i++) {
                users[i] = userJpaRepository.save(new UserFixture().setId(null).create());
                userPoints[i] = userPointJpaRepository.save(
                    new UserPointFixture()
                        .setId(null)
                        .setUserId(users[i].id())
                        .setAmount(20000)
                        .create()
                );
            }

            // 주문 수량 배열 (1~3개)
            int[] quantities = {1, 2, 3, 2, 1};
            int totalQuantity = IntStream.of(quantities).sum(); // 총 주문 수량

            // when
            runConcurrent(userCount, (index) -> {
                OrderCommand.Create command = new OrderCommand.Create(
                    users[index].id(),
                    List.of(new OrderCommand.Create.OrderItem(productOption.id(), quantities[index])),
                    Optional.empty() // 쿠폰 미사용
                );

                orderFacade.process(command);
            });

            // then
            // 재고 확인
            Optional<ProductOption> updatedOption = productOptionJpaRepository.findById(productOption.id());
            assertThat(updatedOption).isPresent();
            assertThat(updatedOption.get().stock()).isEqualTo(100 - totalQuantity);

            // 각 사용자의 포인트 잔액 확인
            for (int i = 0; i < userCount; i++) {
                Optional<UserPoint> updatedPoint = userPointJpaRepository.findById(userPoints[i].id());
                int expectedRemainingPoints = 20000 - (quantities[i] * 5000);

                assertThat(updatedPoint).isPresent();
                assertThat(updatedPoint.get().amount()).isEqualTo(expectedRemainingPoints);
            }

            // 주문 확인
            List<Order> orders = orderJpaRepository.findAll();
            assertThat(orders).hasSize(userCount);
        }
    }
} 