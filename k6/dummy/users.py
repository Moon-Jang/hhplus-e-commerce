import csv
import random
from datetime import datetime

# CSV 파일 경로
USERS_CSV = 'users.csv'
POINTS_CSV = 'user_points.csv'

# 생성할 유저 수
NUM_USERS = 1_000_000

# 포인트 최대값 (0 ~ MAX_POINTS)
MAX_POINTS = 1_000_000

# 현재 시각을 문자열로
now_str = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

# 1) users.csv 생성
with open(USERS_CSV, mode='w', newline='', encoding='utf-8') as users_file:
    writer = csv.writer(users_file)
    # 헤더: id, name, withdrawn_at, created_at, updated_at
    writer.writerow(['id', 'name', 'withdrawn_at', 'created_at', 'updated_at'])

    for user_id in range(1, NUM_USERS + 1):
        name = f'User{user_id:07d}'  # 예: User0000001
        withdrawn_at = ''  # 모두 null (CSV에서 빈 문자열은 NULL로 해석)
        created_at = now_str
        updated_at = now_str
        writer.writerow([user_id, name, withdrawn_at, created_at, updated_at])

print(f"완료: {USERS_CSV} 파일에 {NUM_USERS}개의 유저 레코드 생성됨")

# 2) user_points.csv 생성
with open(POINTS_CSV, mode='w', newline='', encoding='utf-8') as points_file:
    writer = csv.writer(points_file)
    # 헤더: id, user_id, amount, version, created_at, updated_at
    writer.writerow(['id', 'user_id', 'amount', 'version', 'created_at', 'updated_at'])

    for idx, user_id in enumerate(range(1, NUM_USERS + 1), start=1):
        amount = MAX_POINTS
        version = 1
        created_at = now_str
        updated_at = now_str
        writer.writerow([idx, user_id, amount, version, created_at, updated_at])

print(f"완료: {POINTS_CSV} 파일에 {NUM_USERS}개의 포인트 레코드 생성됨")