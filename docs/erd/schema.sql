-- 유저 테이블
-- 기본 유저 정보를 저장합니다.
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '유저 이름',
    `withdrawn_at` TIMESTAMP NULL COMMENT '탈퇴 시간',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`)
) COMMENT='유저 정보 테이블';

-- 유저 포인트 테이블
-- 유저의 포인트 정보를 관리합니다. 포인트 충전 및 결제에 사용합니다.
CREATE TABLE `user_points` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '잔액 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `amount` INT NOT NULL DEFAULT 0 COMMENT '포인트',
    `version` INT NOT NULL DEFAULT 0 COMMENT '버전 (낙관적 락을 위한 버전)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_user_point_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT='유저 포인트 테이블';

-- 포인트 내역 테이블
-- 유저의 포인트 변동 내역을 저장합니다.
CREATE TABLE `user_point_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '히스토리 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `amount` INT NOT NULL COMMENT '변동 금액',
    `type` VARCHAR(30) NOT NULL COMMENT '트랜잭션 타입 (CHARGE: 충전, USE: 사용)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `fk_point_history_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT='포인트 내역 테이블';

-- 쿠폰 테이블
-- 시스템에서 제공하는 쿠폰 정보를 저장합니다.
CREATE TABLE `coupons` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '쿠폰 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '쿠폰 이름',
    `discount_amount` INT NOT NULL COMMENT '할인 금액',
    `issue_start_time` TIMESTAMP NOT NULL COMMENT '발급 시작 시간',
    `issue_end_time` TIMESTAMP NOT NULL COMMENT '발급 종료 시간',
    `max_quantity` INT NOT NULL COMMENT '발급 가능 수량',
    `issued_quantity` INT NOT NULL DEFAULT 0 COMMENT '발급된 수량',
    `expiry_days` INT NOT NULL COMMENT '만료 일수 (발급일로부터 몇일)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`)
) COMMENT='쿠폰 정보 테이블';

-- 유저 쿠폰 테이블
-- 유저에게 발급된 쿠폰 정보를 저장합니다.
CREATE TABLE `issued_coupons` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 쿠폰 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `coupon_id` BIGINT NOT NULL COMMENT '쿠폰 ID',
    `expiry_date` DATE NOT NULL COMMENT '쿠폰 만료 기간',
    `used_at` TIMESTAMP NULL COMMENT '사용 일시',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간 (발급일)',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_coupon_id` (`coupon_id`),
    CONSTRAINT `fk_issued_coupon_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_issued_coupon_coupon_id` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`id`)
) COMMENT='발급된 쿠폰 테이블';

-- 상품 테이블
-- 판매되는 상품 정보를 저장합니다.
CREATE TABLE `products` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '상품 ID',
    `name` VARCHAR(200) NOT NULL COMMENT '상품 이름',
    `description` TEXT NULL COMMENT '상세 설명',
    `price` INT NOT NULL COMMENT '상품 가격',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_price` (`price`)
) COMMENT='상품 정보 테이블';

-- 상품 옵션 테이블
-- 판매되는 상품의 옵션 정보를 저장합니다.
CREATE TABLE `product_options` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '상품 옵션 ID',
    `product_id` BIGINT NOT NULL COMMENT '상품 ID',
    `name` VARCHAR(200) NOT NULL COMMENT '상품 옵션 이름',
    `stock` INT NOT NULL COMMENT '현재 재고 수량',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_product_id` (`product_id`),
    CONSTRAINT `fk_product_option_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) COMMENT='상품 옵션 정보 테이블';

-- 주문 테이블
-- 유저의 주문 정보를 저장합니다.
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `issued_coupon_id` BIGINT NULL COMMENT '사용한 유저 쿠폰 ID',
    `total_amount` INT NOT NULL COMMENT '총 주문 금액 (상품 금액 합계)',
    `discount_amount` INT NOT NULL DEFAULT 0 COMMENT '할인 금액',
    `final_amount` INT NOT NULL COMMENT '최종 결제 금액',
    `status` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '주문 상태 (PENDING: 결제 전, COMPLETED: 완료, REFUNDED: 환불)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간 (주문 시간)',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_issued_coupon_id` (`issued_coupon_id`),
    CONSTRAINT `fk_order_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_order_issued_coupon_id` FOREIGN KEY (`issued_coupon_id`) REFERENCES `issued_coupons` (`id`)
) COMMENT='주문 테이블';

-- 주문 항목 테이블
-- 주문에 포함된 상품 정보를 저장합니다.
CREATE TABLE `order_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 항목 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `product_option_id` BIGINT NOT NULL COMMENT '상품 옵션 ID',
    `product_price` INT NOT NULL COMMENT '주문 당시 상품 가격',
    `quantity` INT NOT NULL COMMENT '구매 수량',
    `amount` INT NOT NULL COMMENT '항목 총액 (가격 × 수량)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_option_id` (`product_option_id`),
    CONSTRAINT `fk_order_item_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    CONSTRAINT `fk_order_item_product_option_id` FOREIGN KEY (`product_option_id`) REFERENCES `product_options` (`id`)
) COMMENT='주문 항목 테이블';

-- 결제 테이블
-- 주문의 결제 내역을 저장합니다.
CREATE TABLE `payments` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '결제 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `amount` INT NOT NULL COMMENT '결제 금액',
    `refund_amount` INT NOT NULL DEFAULT 0 COMMENT '환불 금액',
    `status` VARCHAR(30) NOT NULL COMMENT '결제 상태 (COMPLETED: 완료, REFUNDED: 환불)',
    `method` VARCHAR(30) NOT NULL COMMENT '결제 방법 (POINT: 포인트, CARD: 카드)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `fk_payment_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
    CONSTRAINT `fk_payment_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT='결제 테이블';

-- 결제 실패 내역 테이블
-- 결제 실패에 대한 내역을 저장합니다.
CREATE TABLE `payment_failure_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '결제 실패 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `pay_method` VARCHAR(30) NOT NULL COMMENT '결제 방법 (POINT: 포인트, CARD: 카드)',
    `amount` INT NOT NULL COMMENT '결제 시도 금액',
    `reason` VARCHAR(255) NOT NULL COMMENT '실패 사유',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `fk_payment_failure_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) COMMENT='결제 실패 내역 테이블';