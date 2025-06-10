import csv
import random
from datetime import datetime

# ----------------------------
# 설정 값
# ----------------------------
NUM_PRODUCTS = 10_000      # 생성할 상품 수
MIN_OPTIONS = 1            # 상품당 최소 옵션 개수
MAX_OPTIONS = 3            # 상품당 최대 옵션 개수
FIXED_STOCK = 10_000       # 모든 옵션의 재고 수량

# CSV 파일 경로 (필요 시 절대경로로 수정)
PRODUCTS_CSV_PATH = "products.csv"
OPTIONS_CSV_PATH = "product_options.csv"

# 현재 시각 문자열 (created_at, updated_at으로 사용)
now = datetime.now()
now_str = now.strftime("%Y-%m-%d %H:%M:%S")

# ----------------------------
# 1) products.csv 생성
# ----------------------------
with open(PRODUCTS_CSV_PATH, mode="w", newline="", encoding="utf-8") as products_file:
    writer = csv.writer(products_file)
    # 헤더: id, name, description, price, created_at, updated_at
    writer.writerow([
        "id",
        "name",
        "description",
        "price",
        "created_at",
        "updated_at"
    ])

    for product_id in range(1, NUM_PRODUCTS + 1):
        name = f"Product{product_id:05d}"  # 예: Product00001
        description = f"This is the description for {name}."
        # 가격을 10,000원 ~ 20,000원 사이로 설정
        price = random.randint(10_000, 20_000)

        writer.writerow([
            product_id,
            name,
            description,
            price,
            now_str,
            now_str
        ])

print(f"✅ 완료: {PRODUCTS_CSV_PATH} 에 {NUM_PRODUCTS}개의 상품 데이터 생성됨")


# ----------------------------
# 2) product_options.csv 생성
# ----------------------------
with open(OPTIONS_CSV_PATH, mode="w", newline="", encoding="utf-8") as options_file:
    writer = csv.writer(options_file)
    # 헤더: id, product_id, name, stock, created_at, updated_at
    writer.writerow([
        "id",
        "product_id",
        "name",
        "stock",
        "created_at",
        "updated_at"
    ])

    option_id = 1
    for product_id in range(1, NUM_PRODUCTS + 1):
        # 이 상품에 생성할 옵션 개수: 1 ~ 3개 랜덤
        num_opts = random.randint(MIN_OPTIONS, MAX_OPTIONS)
        for opt_index in range(1, num_opts + 1):
            name = f"Option{opt_index}"
            stock = FIXED_STOCK  # 모든 옵션 재고를 10,000으로 고정

            writer.writerow([
                option_id,
                product_id,
                name,
                stock,
                now_str,
                now_str
            ])
            option_id += 1

print(f"✅ 완료: {OPTIONS_CSV_PATH} 에 총 {option_id - 1}개의 상품 옵션 데이터 생성됨")