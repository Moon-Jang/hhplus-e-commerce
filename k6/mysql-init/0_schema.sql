create table coupons
(
    id               bigint auto_increment comment '쿠폰 ID'
        primary key,
    name             varchar(255)                        null,
    discount_amount  int                                 not null comment '할인 금액',
    issue_start_time timestamp                           not null comment '발급 시작 시간',
    issue_end_time   timestamp                           not null comment '발급 종료 시간',
    max_quantity     int                                 not null comment '발급 가능 수량',
    issued_quantity  int       default 0                 not null comment '발급된 수량',
    expiry_days      int                                 not null comment '만료 일수 (발급일로부터 몇일)',
    created_at       timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at       timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '쿠폰 정보 테이블';

create table daily_product_sales
(
    id               bigint auto_increment
        primary key,
    created_at       datetime(6) default (now()) not null,
    updated_at       datetime(6) default (now()) not null,
    aggregation_date date                        null,
    order_count      bigint                      not null,
    product_id       bigint                      not null,
    constraint daily_product_sales_uk
        unique (product_id, aggregation_date)
);

create index daily_product_sales_covering_index
    on daily_product_sales (aggregation_date, product_id, order_count);

create index daily_product_sales_created_at_index
    on daily_product_sales (created_at);

create table issued_coupons
(
    id          bigint auto_increment comment '유저 쿠폰 ID'
        primary key,
    user_id     bigint                              not null comment '유저 ID',
    coupon_id   bigint                              not null comment '쿠폰 ID',
    expiry_date date                                not null comment '쿠폰 만료 기간',
    used_at     timestamp                           null comment '사용 일시',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '생성 시간 (발급일)',
    updated_at  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '발급된 쿠폰 테이블';

create index idx_coupon_id
    on issued_coupons (coupon_id);

create index idx_user_id
    on issued_coupons (user_id);

create table order_items
(
    id                bigint auto_increment comment '주문 항목 ID'
        primary key,
    order_id          bigint                              not null comment '주문 ID',
    product_option_id bigint                              not null comment '상품 옵션 ID',
    product_price     int                                 not null comment '주문 당시 상품 가격',
    quantity          int                                 not null comment '구매 수량',
    amount            int                                 not null comment '항목 총액 (가격 × 수량)',
    created_at        timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at        timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '주문 항목 테이블';

create index idx_order_id
    on order_items (order_id);

create index order_items_product_option_id_index
    on order_items (product_option_id);

create table orders
(
    id               bigint auto_increment comment '주문 ID'
        primary key,
    user_id          bigint                                not null comment '유저 ID',
    issued_coupon_id bigint                                null comment '사용한 유저 쿠폰 ID',
    total_amount     int                                   not null comment '총 주문 금액 (상품 금액 합계)',
    discount_amount  decimal(38, 2)                        null,
    final_amount     decimal(38, 2)                        null,
    status           varchar(30) default 'PENDING'         not null comment '주문 상태 (PENDING: 결제 전, COMPLETED: 완료, REFUNDED: 환불)',
    created_at       timestamp   default CURRENT_TIMESTAMP not null comment '생성 시간 (주문 시간)',
    updated_at       timestamp   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '주문 테이블';

create index idx_issued_coupon_id
    on orders (issued_coupon_id);

create index idx_user_id
    on orders (user_id);

create index idx_user_id
    on payment_failure_histories (user_id);

create table payments
(
    id            bigint auto_increment comment '결제 ID'
        primary key,
    order_id      bigint                              not null comment '주문 ID',
    user_id       bigint                              not null comment '유저 ID',
    amount        int                                 not null comment '결제 금액',
    refund_amount int       default 0                 not null comment '환불 금액',
    status        varchar(30)                         not null comment '결제 상태 (COMPLETED: 완료, REFUNDED: 환불)',
    created_at    timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간',
    constraint uk_order_id
        unique (order_id)
)
    comment '결제 테이블';

create index idx_user_id
    on payments (user_id);

create table product_options
(
    id         bigint auto_increment comment '상품 옵션 ID'
        primary key,
    product_id bigint                              not null comment '상품 ID',
    name       varchar(200)                        not null comment '상품 옵션 이름',
    stock      int                                 not null comment '현재 재고 수량',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '상품 옵션 정보 테이블';

create index product_options_product_id_index
    on product_options (product_id);

create table products
(
    id          bigint auto_increment comment '상품 ID'
        primary key,
    name        varchar(200)                        not null comment '상품 이름',
    description text                                null comment '상세 설명',
    price       int                                 not null comment '상품 가격',
    created_at  timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at  timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '상품 정보 테이블';

create index idx_price
    on products (price);

create table shedlock
(
    name       varchar(64)                               not null
        primary key,
    lock_until timestamp(3)                              not null,
    locked_at  timestamp(3) default CURRENT_TIMESTAMP(3) not null,
    locked_by  varchar(255)                              not null
);

create table user_point_histories
(
    id         bigint auto_increment comment '히스토리 ID'
        primary key,
    user_id    bigint                              not null comment '유저 ID',
    amount     int                                 not null comment '변동 금액',
    type       varchar(30)                         not null comment '트랜잭션 타입 (CHARGE: 충전, USE: 사용)',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '포인트 내역 테이블';

create index idx_user_id
    on user_point_histories (user_id);

create table user_points
(
    id         bigint auto_increment comment '잔액 ID'
        primary key,
    user_id    bigint                              not null comment '유저 ID',
    amount     int       default 0                 not null comment '포인트',
    version    int       default 0                 not null comment '버전 (낙관적 락을 위한 버전)',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간',
    constraint uk_user_id
        unique (user_id)
)
    comment '유저 포인트 테이블';

create table users
(
    id           bigint auto_increment comment '유저 ID'
        primary key,
    name         varchar(100)                        not null comment '유저 이름',
    withdrawn_at timestamp                           null comment '탈퇴 시간',
    created_at   timestamp default CURRENT_TIMESTAMP not null comment '생성 시간',
    updated_at   timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '수정 시간'
)
    comment '유저 정보 테이블';

