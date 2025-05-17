package kr.hhplus.ecommerce.infrastructure.coupon;

import org.springframework.data.repository.CrudRepository;

public interface IssuedCouponRedisRepository extends CrudRepository<IssuedCouponRedisEntity, String>, IssuedCouponRedisRepositoryCustom {
}