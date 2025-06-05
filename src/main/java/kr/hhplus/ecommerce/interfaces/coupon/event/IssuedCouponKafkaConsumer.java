package kr.hhplus.ecommerce.interfaces.coupon.event;

import kr.hhplus.ecommerce.common.constant.Topics;
import kr.hhplus.ecommerce.domain.coupon.CouponCommand;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IssuedCouponKafkaConsumer {
    private final IssuedCouponService issuedCouponService;

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 2.0),
        dltTopicSuffix = Topics.ISSUE_COUPON + ".dlt"
    )
    @KafkaListener(
        topics = Topics.ISSUE_COUPON,
        groupId = "coupon"
    )
    public void handleIssueCouponEvent(@Payload IssueCouponPayload.Issue payload) {
        CouponCommand.Issue command = new CouponCommand.Issue(
            payload.userId(),
            payload.couponId()
        );

        issuedCouponService.issue(command);
    }

    @KafkaListener(
        topics = Topics.ISSUE_COUPON + ".dlt",
        groupId = "coupon"
    )
    public void handleDeadLetterTopic(@Payload IssueCouponPayload.Issue payload)  {
        log.error("Failed to process DLT message payload: {}", payload);
    }
}
