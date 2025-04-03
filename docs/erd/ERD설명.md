# ERD 설명

## 테이블 구조

### 1. 유저 테이블 (user)

| 컬럼명     | 데이터 타입  | 설명      | 제약조건                                      |
| ---------- | ------------ | --------- | --------------------------------------------- |
| id         | BIGINT       | 유저 ID   | PRIMARY KEY, AUTO_INCREMENT                   |
| name       | VARCHAR(100) | 유저 이름 | NOT NULL                                      |
| left_at    | TIMESTAMP    | 탈퇴 시간 | NULL 허용                                     |
| created_at | TIMESTAMP    | 생성 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at | TIMESTAMP    | 수정 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 기본 유저 정보를 저장하는 테이블입니다.

### 2. 유저 포인트 테이블 (user_point)

| 컬럼명     | 데이터 타입 | 설명      | 제약조건                                      |
| ---------- | ----------- | --------- | --------------------------------------------- |
| id         | BIGINT      | 잔액 ID   | PRIMARY KEY, AUTO_INCREMENT                   |
| user_id    | BIGINT      | 유저 ID   | NOT NULL, FOREIGN KEY(user.id), UNIQUE        |
| balance    | INT         | 포인트    | NOT NULL, DEFAULT 0                           |
| created_at | TIMESTAMP   | 생성 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at | TIMESTAMP   | 수정 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 유저의 잔액 정보를 관리하는 테이블로, 잔액 충전 및 결제에 사용됩니다.

### 3. 포인트 히스토리 테이블 (user_point_history)

| 컬럼명     | 데이터 타입 | 설명          | 제약조건                                      |
| ---------- | ----------- | ------------- | --------------------------------------------- |
| id         | BIGINT      | 히스토리 ID   | PRIMARY KEY, AUTO_INCREMENT                   |
| user_id    | BIGINT      | 유저 ID       | NOT NULL, FOREIGN KEY(user.id), INDEX         |
| amount     | INT         | 변동 금액     | NOT NULL                                      |
| type       | VARCHAR(30) | 트랜잭션 타입 | NOT NULL (CHARGE: 충전, USE: 사용)            |
| created_at | TIMESTAMP   | 생성 시간     | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at | TIMESTAMP   | 수정 시간     | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 유저의 잔액 변동 내역을 기록하는 테이블입니다.

### 4. 쿠폰 정보 테이블 (coupon)

| 컬럼명           | 데이터 타입  | 설명           | 제약조건                                      |
| ---------------- | ------------ | -------------- | --------------------------------------------- |
| id               | BIGINT       | 쿠폰 ID        | PRIMARY KEY, AUTO_INCREMENT                   |
| name             | VARCHAR(100) | 쿠폰 이름      | NOT NULL                                      |
| discount_amount  | INT          | 할인 금액      | NOT NULL                                      |
| issue_start_time | TIMESTAMP    | 발급 시작 시간 | NOT NULL                                      |
| issue_end_time   | TIMESTAMP    | 발급 종료 시간 | NOT NULL                                      |
| max_quantity     | INT          | 발급 가능 수량 | NOT NULL                                      |
| issued_quantity  | INT          | 발급된 수량    | NOT NULL, DEFAULT 0                           |
| expiry_days      | INT          | 만료 일수      | NOT NULL (발급일로부터 X일)                   |
| created_at       | TIMESTAMP    | 생성 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at       | TIMESTAMP    | 수정 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 시스템에서 제공하는 쿠폰 정보를 저장하는 테이블입니다.

### 5. 발급된 쿠폰 테이블 (issued_coupon)

| 컬럼명      | 데이터 타입 | 설명               | 제약조건                                      |
| ----------- | ----------- | ------------------ | --------------------------------------------- |
| id          | BIGINT      | 유저 쿠폰 ID       | PRIMARY KEY, AUTO_INCREMENT                   |
| user_id     | BIGINT      | 유저 ID            | NOT NULL, FOREIGN KEY(user.id), INDEX         |
| coupon_id   | BIGINT      | 쿠폰 ID            | NOT NULL, FOREIGN KEY(coupon.id), INDEX       |
| expiry_time | TIMESTAMP   | 유효기간           | NOT NULL                                      |
| used_at     | TIMESTAMP   | 사용 일시          | NULL 허용                                     |
| created_at  | TIMESTAMP   | 생성 시간 (발급일) | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at  | TIMESTAMP   | 수정 시간          | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 유저에게 발급된 쿠폰 정보를 저장하는 테이블입니다.

### 6. 상품 정보 테이블 (product)

| 컬럼명           | 데이터 타입  | 설명           | 제약조건                                      |
| ---------------- | ------------ | -------------- | --------------------------------------------- |
| id               | BIGINT       | 상품 ID        | PRIMARY KEY, AUTO_INCREMENT                   |
| name             | VARCHAR(200) | 상품 이름      | NOT NULL                                      |
| price            | INT          | 상품 가격      | NOT NULL, INDEX                               |
| total_quantity   | INT          | 총 재고 수량   | NOT NULL                                      |
| current_quantity | INT          | 현재 재고 수량 | NOT NULL                                      |
| description      | TEXT         | 상세 설명      | NULL 허용                                     |
| created_at       | TIMESTAMP    | 생성 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at       | TIMESTAMP    | 수정 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 판매되는 상품 정보를 저장하는 테이블입니다.

### 7. 일일 상품 판매 통계 테이블 (daily_product_statistics)

| 컬럼명           | 데이터 타입 | 설명           | 제약조건                                      |
| ---------------- | ----------- | -------------- | --------------------------------------------- |
| id               | BIGINT      | 통계 ID        | PRIMARY KEY, AUTO_INCREMENT                   |
| product_id       | BIGINT      | 상품 ID        | NOT NULL, FOREIGN KEY(product.id), INDEX      |
| aggregation_date | DATE        | 집계 날짜      | NOT NULL, INDEX                               |
| sales_count      | INT         | 일일 판매량    | NOT NULL, DEFAULT 0                           |
| sales_amount     | INT         | 일일 판매 금액 | NOT NULL, DEFAULT 0                           |
| created_at       | TIMESTAMP   | 생성 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at       | TIMESTAMP   | 수정 시간      | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 상품별 일별 판매 통계를 저장하는 테이블로, 인기 상품 조회에 활용됩니다.

### 8. 주문 테이블 (order)

| 컬럼명           | 데이터 타입  | 설명                  | 제약조건                                        |
| ---------------- | ------------ | --------------------- | ----------------------------------------------- |
| id               | BIGINT       | 주문 ID               | PRIMARY KEY, AUTO_INCREMENT                     |
| user_id          | BIGINT       | 유저 ID               | NOT NULL, FOREIGN KEY(user.id), INDEX           |
| issued_coupon_id | BIGINT       | 사용한 유저 쿠폰 ID   | NULL 허용, FOREIGN KEY(issued_coupon.id), INDEX |
| status           | VARCHAR(30)  | 주문 상태             | NOT NULL (COMPLETED: 완료, FAILED: 실패)        |
| total_amount     | INT          | 총 주문 금액          | NOT NULL (상품 금액 합계)                       |
| discount_amount  | INT          | 할인 금액             | NOT NULL, DEFAULT 0                             |
| final_amount     | INT          | 최종 결제 금액        | NOT NULL                                        |
| failed_reason    | VARCHAR(200) | 실패 이유             | NULL 허용                                       |
| created_at       | TIMESTAMP    | 생성 시간 (주문 시간) | NOT NULL, DEFAULT CURRENT_TIMESTAMP             |
| updated_at       | TIMESTAMP    | 수정 시간             | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE   |

**설명**: 유저의 주문 정보를 저장하는 테이블입니다.

### 9. 주문 항목 테이블 (order_item)

| 컬럼명        | 데이터 타입 | 설명                | 제약조건                                      |
| ------------- | ----------- | ------------------- | --------------------------------------------- |
| id            | BIGINT      | 주문 항목 ID        | PRIMARY KEY, AUTO_INCREMENT                   |
| order_id      | BIGINT      | 주문 ID             | NOT NULL, FOREIGN KEY(order.id), INDEX        |
| product_id    | BIGINT      | 상품 ID             | NOT NULL, FOREIGN KEY(product.id), INDEX      |
| product_price | INT         | 주문 당시 상품 가격 | NOT NULL                                      |
| quantity      | INT         | 구매 수량           | NOT NULL                                      |
| amount        | INT         | 항목 총액           | NOT NULL (가격 × 수량)                        |
| created_at    | TIMESTAMP   | 생성 시간           | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at    | TIMESTAMP   | 수정 시간           | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 주문에 포함된 상품 정보를 저장하는 테이블입니다.

### 10. 결제 테이블 (payment)

| 컬럼명     | 데이터 타입 | 설명      | 제약조건                                      |
| ---------- | ----------- | --------- | --------------------------------------------- |
| id         | BIGINT      | 결제 ID   | PRIMARY KEY, AUTO_INCREMENT                   |
| order_id   | BIGINT      | 주문 ID   | NOT NULL, FOREIGN KEY(order.id), UNIQUE       |
| user_id    | BIGINT      | 유저 ID   | NOT NULL, FOREIGN KEY(user.id), INDEX         |
| amount     | INT         | 결제 금액 | NOT NULL                                      |
| status     | VARCHAR(30) | 결제 상태 | NOT NULL (COMPLETED: 완료, FAILED: 실패)      |
| payed_at   | TIMESTAMP   | 결제 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP, INDEX    |
| created_at | TIMESTAMP   | 생성 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP           |
| updated_at | TIMESTAMP   | 수정 시간 | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE |

**설명**: 주문의 결제 내역을 저장하는 테이블입니다.
