package kr.hhplus.ecommerce.infrastructure.coupon;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IssuedCouponRedisRepository extends CrudRepository<IssuedCouponRedisEntity, String>, IssuedCouponRedisRepositoryCustom {
}