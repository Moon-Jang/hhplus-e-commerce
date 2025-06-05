import csv
import random
from datetime import datetime, timedelta

# ----------------------------
# 설정 값
# ----------------------------
NUM_USERS = 1_000_000        # 유저 수
NUM_COUPONS = 10_000         # 쿠폰 수
# 각 유저당 발급 쿠폰 개수 범위 (1 ~ 3개)
MIN_ISSUED_PER_USER = 1
MAX_ISSUED_PER_USER = 3

# CSV 파일 경로 (필요시 절대경로로 변경)
COUPONS_CSV_PATH = "coupons.csv"
ISSUED_CSV_PATH = "issued_coupons.csv"

# 현재 시각 문자열 (created_at, updated_at으로 사용)
now = datetime.now()
now_str = now.strftime("%Y-%m-%d %H:%M:%S")
today_date = now.date()

# ----------------------------
# 1) coupons.csv 생성
# ----------------------------
with open(COUPONS_CSV_PATH, mode="w", newline="", encoding="utf-8") as coupons_file:
    writer = csv.writer(coupons_file)
    # 헤더: id, name, discount_amount, issue_start_time, issue_end_time,
    #       max_quantity, issued_quantity, expiry_days, created_at, updated_at
    writer.writerow([
        "id",
        "name",
        "discount_amount",
        "issue_start_time",
        "issue_end_time",
        "max_quantity",
        "issued_quantity",
        "expiry_days",
        "created_at",
        "updated_at"
    ])

    for coupon_id in range(1, NUM_COUPONS + 1):
        # 예시값 설정
        name = f"Coupon{coupon_id:05d}"
        discount_amount = random.randint(1000, 10000)        # 1,000원~10,000원 사이 랜덤 할인액
        # 발급 가능 기간: 2025-01-01 ~ 2025-12-31 (UTC)
        issue_start_time = datetime(2025, 1, 1, 0, 0, 0)     # 고정값
        issue_end_time   = datetime(2025, 12, 31, 23, 59, 59)
        # 최대 발급 수량: 1,000 ~ 10,000 사이 랜덤
        max_quantity    = random.randint(1_000, 10_000)
        # 현재 발급된 수량: 0 ~ max_quantity 사이 랜덤
        issued_quantity = random.randint(0, max_quantity)
        # 만료 일수: 7 ~ 90일 사이 랜덤
        expiry_days     = random.randint(7, 90)

        # 작성할 때 문자열 형식으로 변환
        issue_start_str = issue_start_time.strftime("%Y-%m-%d %H:%M:%S")
        issue_end_str   = issue_end_time.strftime("%Y-%m-%d %H:%M:%S")

        writer.writerow([
            coupon_id,
            name,
            discount_amount,
            issue_start_str,
            issue_end_str,
            max_quantity,
            issued_quantity,
            expiry_days,
            now_str,
            now_str
        ])

print(f"✅ 완료: {COUPONS_CSV_PATH} 파일에 {NUM_COUPONS}개의 쿠폰 데이터 생성됨")


# ----------------------------
# 2) issued_coupons.csv 생성
# ----------------------------
with open(ISSUED_CSV_PATH, mode="w", newline="", encoding="utf-8") as issued_file:
    writer = csv.writer(issued_file)
    # 헤더: id, user_id, coupon_id, expiry_date, used_at, created_at, updated_at
    writer.writerow([
        "id",
        "user_id",
        "coupon_id",
        "expiry_date",
        "used_at",
        "created_at",
        "updated_at"
    ])

    issued_id = 1
    for user_id in range(1, NUM_USERS + 1):
        # 이 유저가 받는 쿠폰 개수: 1 ~ 3개 랜덤
        count = random.randint(MIN_ISSUED_PER_USER, MAX_ISSUED_PER_USER)
        for _ in range(count):
            coupon_id = random.randint(1, NUM_COUPONS)
            # 만료일: 발급일(=오늘) + 쿠폰 만료일수 (1~90일 중 랜덤)
            # 주의: 실제 만료일수는 coupons 테이블의 expiry_days를 참고해야 하나,
            # 여기서는 랜덤하게 7~90일 중 하나를 사용해 생성합니다.
            random_expiry_days = random.randint(7, 90)
            expiry_date = today_date + timedelta(days=random_expiry_days)
            expiry_str = expiry_date.strftime("%Y-%m-%d")

            # 사용 일시(used_at)는 아직 사용 안 했다는 의미로 빈 문자열
            used_at = ""

            writer.writerow([
                issued_id,
                user_id,
                coupon_id,
                expiry_str,
                used_at,
                now_str,
                now_str
            ])
            issued_id += 1

print(f"✅ 완료: {ISSUED_CSV_PATH} 파일에 총 {issued_id - 1}개의 발급된 쿠폰 데이터 생성됨")