-- 유저 테이블
-- 기본 유저 정보를 저장합니다.
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '유저 이름',
    `left_at` TIMESTAMP NULL COMMENT '탈퇴 시간',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`)
) COMMENT='유저 정보 테이블';

-- 잔액 테이블
-- 유저의 잔액 정보를 관리합니다. 잔액 충전 및 결제에 사용합니다.
CREATE TABLE `user_point` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '잔액 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `balance` INT NOT NULL DEFAULT 0 COMMENT '포인트',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_user_point_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) COMMENT='유저 포인트 테이블';

-- 잔액 내역 테이블
-- 유저의 잔액 변동 내역을 저장합니다.
CREATE TABLE `user_point_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '히스토리 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `amount` INT NOT NULL COMMENT '변동 금액',
    `type` VARCHAR(30) NOT NULL COMMENT '트랜잭션 타입 (CHARGE: 충전, USE: 사용)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    CONSTRAINT `fk_point_history_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) COMMENT='포인트 히스토리 테이블';

-- 쿠폰 테이블
-- 시스템에서 제공하는 쿠폰 정보를 저장합니다.
CREATE TABLE `coupon` (
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
CREATE TABLE `issued_coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '유저 쿠폰 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `coupon_id` BIGINT NOT NULL COMMENT '쿠폰 ID',
    `expiry_time` TIMESTAMP NOT NULL COMMENT '유효기간',
    `used_at` TIMESTAMP NULL COMMENT '사용 일시',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간 (발급일)',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_coupon_id` (`coupon_id`),
    CONSTRAINT `fk_issued_coupon_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_issued_coupon_coupon_id` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`)
) COMMENT='발급된 쿠폰 테이블';

-- 상품 테이블
-- 판매되는 상품 정보를 저장합니다.
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '상품 ID',
    `name` VARCHAR(200) NOT NULL COMMENT '상품 이름',
    `price` INT NOT NULL COMMENT '상품 가격',
    `total_quantity` INT NOT NULL COMMENT '총 재고 수량',
    `current_quantity` INT NOT NULL COMMENT '현재 재고 수량',
    `description` TEXT NULL COMMENT '상세 설명',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_price` (`price`)
) COMMENT='상품 정보 테이블';

-- 일일 상품 판매 통계 테이블
-- 상품별 일별 판매 통계를 저장합니다. 인기 상품 조회에 활용됩니다.
CREATE TABLE `daily_product_statistics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '통계 ID',
    `product_id` BIGINT NOT NULL COMMENT '상품 ID',
    `aggregation_date` DATE NOT NULL COMMENT '집계 날짜',
    `sales_count` INT NOT NULL DEFAULT 0 COMMENT '일일 판매량',
    `sales_amount` INT NOT NULL DEFAULT 0 COMMENT '일일 판매 금액',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_date` (`product_id`, `aggregation_date`),
    INDEX `idx_aggregation_date` (`aggregation_date`),
    INDEX `idx_product_id` (`product_id`),
    CONSTRAINT `fk_daily_statistics_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) COMMENT='일일 상품 판매 통계 테이블';

-- 주문 테이블
-- 유저의 주문 정보를 저장합니다.
CREATE TABLE `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `issued_coupon_id` BIGINT NULL COMMENT '사용한 유저 쿠폰 ID',
    `status` VARCHAR(30) NOT NULL COMMENT '주문 상태 (COMPLETED: 완료, FAILED: 실패)',
    `total_amount` INT NOT NULL COMMENT '총 주문 금액 (상품 금액 합계)',
    `discount_amount` INT NOT NULL DEFAULT 0 COMMENT '할인 금액',
    `final_amount` INT NOT NULL COMMENT '최종 결제 금액',
    `failed_reason` VARCHAR(200) NULL COMMENT '실패 이유',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간 (주문 시간)',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_issued_coupon_id` (`issued_coupon_id`),
    CONSTRAINT `fk_order_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_order_issued_coupon_id` FOREIGN KEY (`issued_coupon_id`) REFERENCES `issued_coupon` (`id`)
) COMMENT='주문 테이블';

-- 주문 항목 테이블
-- 주문에 포함된 상품 정보를 저장합니다.
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '주문 항목 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `product_id` BIGINT NOT NULL COMMENT '상품 ID',
    `product_price` INT NOT NULL COMMENT '주문 당시 상품 가격',
    `quantity` INT NOT NULL COMMENT '구매 수량',
    `amount` INT NOT NULL COMMENT '항목 총액 (가격 × 수량)',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_product_id` (`product_id`),
    CONSTRAINT `fk_order_item_order_id` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
    CONSTRAINT `fk_order_item_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) COMMENT='주문 항목 테이블';

-- 결제 테이블
-- 주문의 결제 내역을 저장합니다.
CREATE TABLE `payment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '결제 ID',
    `order_id` BIGINT NOT NULL COMMENT '주문 ID',
    `user_id` BIGINT NOT NULL COMMENT '유저 ID',
    `amount` INT NOT NULL COMMENT '결제 금액',
    `status` VARCHAR(30) NOT NULL COMMENT '결제 상태 (COMPLETED: 완료, FAILED: 실패)',
    `payed_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '결제 시간',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_payed_at` (`payed_at`),
    CONSTRAINT `fk_payment_order_id` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
    CONSTRAINT `fk_payment_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) COMMENT='결제 테이블';