package kr.hhplus.ecommerce.infrastructure.coupon;

import kr.hhplus.ecommerce.common.constant.Topics;
import kr.hhplus.ecommerce.common.support.utils.JsonUtils;
import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequest;
import kr.hhplus.ecommerce.domain.coupon.CouponIssuanceRequestPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssuanceRequestKafkaPublisher implements CouponIssuanceRequestPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(CouponIssuanceRequest request) {
        String partitionKey = String.valueOf(request.couponId());

        kafkaTemplate.send(
            Topics.ISSUE_COUPON,
            partitionKey,
            JsonUtils.stringify(request)
        );
    }
}
