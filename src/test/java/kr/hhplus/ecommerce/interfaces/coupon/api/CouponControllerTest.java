package kr.hhplus.ecommerce.interfaces.coupon.api;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import kr.hhplus.ecommerce.domain.coupon.CouponFixture;
import kr.hhplus.ecommerce.domain.coupon.CouponService;
import kr.hhplus.ecommerce.domain.coupon.CouponVo;
import kr.hhplus.ecommerce.domain.coupon.IssuedCouponService;
import kr.hhplus.ecommerce.interfaces.ControllerTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.fieldsWithBasic;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

class CouponControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.COUPON.tagName();

    @MockitoBean
    private CouponService couponService;
    @MockitoBean
    private IssuedCouponService issuedCouponService;

    @Nested
    @DisplayName("선착순 쿠폰 발급")
    class IssueCouponTest {
        private static final String DESCRIPTION = Tags.COUPON.descriptionWith("발급");

        @Test
        void 쿠폰_발급_성공() throws Exception {
            // given
            var request = new CouponRequest.Issue(1L);

            // when/then
            given()
                .body(request)
                .when()
                .post("/v1/coupons/{couponId}/issue", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("issueCoupon"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION)
                            .requestFields(
                                fieldWithPath("userId").type(NUMBER).description("사용자 ID")
                            )
                            .responseFields(
                                fieldsWithBasic(
                                    fieldWithPath("data").type(NULL).description("응답 데이터")
                                )
                            )
                        )
                )
                .status(HttpStatus.OK);
        }
    }
    
    @Nested
    @DisplayName("발급 가능한 쿠폰 목록 조회")
    class GetAvailableCouponsTest {
        private static final String DESCRIPTION = Tags.COUPON.descriptionWith("발급 가능한 목록 조회");

        @Test
        void 발급_가능한_쿠폰_목록_조회_성공() throws Exception {
            // given
            CouponVo coupon = CouponVo.from(new CouponFixture().create());
            doReturn(List.of(coupon))
                .when(couponService)
                .getAvailableCoupons();

            // when/then
            given()
                .when()
                .get("/v1/coupons/available")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("getAvailableCoupons"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION)
                            .responseFields(
                                fieldsWithBasic(
                                    fieldWithPath("data").type(ARRAY).description("쿠폰 목록"),
                                    fieldWithPath("data[].id").type(NUMBER).description("쿠폰 ID"),
                                    fieldWithPath("data[].name").type(STRING).description("쿠폰 이름"),
                                    fieldWithPath("data[].discountAmount").type(NUMBER).description("할인 금액"),
                                    fieldWithPath("data[].issueStartTime").type(STRING).description("쿠폰 발급 시작 시간"),
                                    fieldWithPath("data[].issueEndTime").type(STRING).description("쿠폰 발급 종료 시간"),
                                    fieldWithPath("data[].maxQuantity").type(NUMBER).description("최대 발급 수량"),
                                    fieldWithPath("data[].currentQuantity").type(NUMBER).description("현재 발급된 수량")
                                )
                            )
                        )
                )
                .status(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("쿠폰 상세 조회")
    class GetCouponDetailTest {
        private static final String DESCRIPTION = Tags.COUPON.descriptionWith("상세 조회");

        @Test
        void 쿠폰_상세_조회_성공() throws Exception {
            // given
            CouponVo coupon = CouponVo.from(new CouponFixture().create());
            doReturn(coupon)
                .when(couponService)
                .getCouponDetail(anyLong());

            // when/then
            given()
                .when()
                .get("/v1/coupons/{couponId}", 1L)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("getCouponDetail"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION)
                            .responseFields(
                                fieldsWithBasic(
                                    fieldWithPath("data").type(OBJECT).description("쿠폰 상세 정보"),
                                    fieldWithPath("data.id").type(NUMBER).description("쿠폰 ID"),
                                    fieldWithPath("data.name").type(STRING).description("쿠폰 이름"),
                                    fieldWithPath("data.discountAmount").type(NUMBER).description("할인 금액"),
                                    fieldWithPath("data.issueStartTime").type(STRING).description("쿠폰 발급 시작 시간"),
                                    fieldWithPath("data.issueEndTime").type(STRING).description("쿠폰 발급 종료 시간"),
                                    fieldWithPath("data.maxQuantity").type(NUMBER).description("최대 발급 수량"),
                                    fieldWithPath("data.currentQuantity").type(NUMBER).description("현재 발급된 수량")
                                )
                            )
                        )
                )
                .status(HttpStatus.OK);
        }
    }
} 