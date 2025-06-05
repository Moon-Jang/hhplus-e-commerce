import http from 'k6/http';
import { sleep, check, fail } from 'k6';

const HOST = 'http://localhost:8080';

export let options = {
    stages: [
        {duration: '10s', target: 80},
        {duration: '1m', target: 100},
        {duration: '10s', target: 80},
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
};

export default function() {
    // 1. 매 반복마다 1 ~ 1,000,000 사이의 랜덤 유저 ID 생성
    const userId = Math.floor(Math.random() * 1_000_000) + 1;

    // 2. 인기 상품 조회
    const topProducts = getTopSellingProducts(10);
    if (!Array.isArray(topProducts) || topProducts.length === 0) {
        sleep(1)
        return;
    }

    // 3. 인기 상품 중 랜덤으로 하나 선택
    const randomIndex = Math.floor(Math.random() * topProducts.length);
    const randomProduct = topProducts[randomIndex];
    const productId = randomProduct.id;

    // 4. 상품 상세 조회 (옵션 정보 포함)
    const productDetails = getProductDetails(productId);
    if (!productDetails) {
        sleep(1)
        return;
    }

    // 5. 상세 조회 결과에서 옵션 ID 추출 (첫 번째 옵션 선택)
    let productOptionId = null;
    if (Array.isArray(productDetails.options) && productDetails.options.length > 0) {
        productOptionId = productDetails.options[0].id;
    }
    if (productOptionId === null) {
        sleep(1)
        return;
    }

    // 6. 해당 랜덤 유저의 발급된 쿠폰 목록 조회
    const issuedCoupons = getIssuedCoupons(userId);
    let couponIdToUse = null;
    if (Array.isArray(issuedCoupons) && issuedCoupons.length > 0) {
        couponIdToUse = issuedCoupons[0].id;
    }

    // 7. 주문 생성
    createOrder(userId, productOptionId, couponIdToUse);
    sleep(1)
}

function getTopSellingProducts(limit = 10) {
    const res = http.get(`${HOST}/v1/products/top-selling?limit=${limit}`);
    const passed = check(res, {
        '1. 인기상품 조회: status 200': (r) => r.status === 200,
    });
    if (!passed) {
        fail(`인기상품 조회 실패: status ${res.status}`);
    }

    try {
        return res.json().data || [];
    } catch (e) {
        return [];
    }
}

function getProductDetails(productId) {
    const res = http.get(`${HOST}/v1/products/${productId}`);
    const passed = check(res, {
        '2. 상품 상세 조회: status 200': (r) => r.status === 200,
    });
    if (!passed) {
        fail(`상품 상세 조회 실패 (productId=${productId}): status ${res.status}`);
    }

    try {
        return res.json().data || null;
    } catch (e) {
        return null;
    }
}

function getIssuedCoupons(userId) {
    const res = http.get(`${HOST}/v1/issued-coupons?userId=${userId}`);
    const passed = check(res, {
        '3. 발급된 쿠폰 조회: status 200': (r) => r.status === 200,
    });
    if (!passed) {
        fail(`발급된 쿠폰 조회 실패 (userId=${userId}): status ${res.status}`);
    }

    try {
        return res.json().data || [];
    } catch (e) {
        return [];
    }
}

function createOrder(userId, productOptionId, issuedCouponId = null) {
    const payload = {
        userId: parseInt(userId, 10),
        items: [
            {
                productOptionId: parseInt(productOptionId, 10),
                quantity: 1,
            },
        ],
        issuedCouponId: issuedCouponId !== null ? parseInt(issuedCouponId, 10) : null,
    };

    const headers = { 'Content-Type': 'application/json' };
    const res = http.post(`${HOST}/v1/orders`, JSON.stringify(payload), { headers });

    const passed = check(res, {
        '4. 주문 생성: status 200': (r) => r.status === 200,
        '4. 주문 생성: 응답에 id 포함됨': (r) => {
            try {
                return typeof r.json().data.id !== 'undefined';
            } catch (e) {
                return false;
            }
        },
    });
    if (!passed) {
        fail(
            `주문 생성 실패 (userId=${userId}, productOptionId=${productOptionId}, couponId=${issuedCouponId}): status ${res.status}`
        );
    }

    try {
        return res.json().data || null;
    } catch (e) {
        return null;
    }
}