import csv
import random
from datetime import datetime, date, timedelta

# ----------------------------
# 설정 값
# ----------------------------
NUM_PRODUCTS = 10_000

# 집계 시작일 / 종료일 (YYYY, M, D)
START_DATE = date(2025, 6, 1)
END_DATE   = date(2025, 6, 5)

# 생성할 CSV 파일 경로 (필요 시 절대경로로 수정)
OUTPUT_CSV_PATH = "daily_product_sales.csv"

# 현재 시각 (created_at, updated_at으로 모두 사용)
now = datetime.now()
now_str = now.strftime("%Y-%m-%d %H:%M:%S.%f")  # datetime(6) 정밀도까지 허용

# ----------------------------
# 1) CSV 생성
# ----------------------------
with open(OUTPUT_CSV_PATH, mode="w", newline="", encoding="utf-8") as csvfile:
    writer = csv.writer(csvfile)
    # 헤더: id, created_at, updated_at, aggregation_date, order_count, product_id
    writer.writerow([
        "id",
        "created_at",
        "updated_at",
        "aggregation_date",
        "order_count",
        "product_id"
    ])

    row_id = 1
    current_date = START_DATE
    while current_date <= END_DATE:
        agg_date_str = current_date.strftime("%Y-%m-%d")
        for product_id in range(1, NUM_PRODUCTS + 1):
            # 예시로 주문 건수를 0~100 사이 랜덤으로 설정
            order_count = random.randint(0, 100)

            writer.writerow([
                row_id,
                now_str,
                now_str,
                agg_date_str,
                order_count,
                product_id
            ])
            row_id += 1

        current_date += timedelta(days=1)

print(f"✅ 완료: {OUTPUT_CSV_PATH} 파일에 총 {row_id - 1}개의 레코드 생성됨")