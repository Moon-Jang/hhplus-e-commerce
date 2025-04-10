package kr.hhplus.ecommerce.interfaces.order;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import kr.hhplus.ecommerce.application.order.OrderFacade;
import kr.hhplus.ecommerce.domain.order.OrderVoFixture;
import kr.hhplus.ecommerce.domain.payment.Payment;
import kr.hhplus.ecommerce.interfaces.ControllerTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class OrderControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.ORDER.tagName();

    @MockitoBean
    private OrderFacade orderFacade;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrderTest {
        private static final String DESCRIPTION = Tags.ORDER.descriptionWith("주문/결제");

        @Test
        void 성공() {
            // given
            var request = new OrderRequest.Create(
                1L,
                List.of(new OrderRequest.Create.OrderItem(1L, 2)),
                Optional.empty(),
                Payment.Method.CARD
            );
            doReturn(new OrderVoFixture().create())
                .when(orderFacade)
                .process(any(), any());

            // when/then
            given()
                .header(authorizationHeader())
                .body(request)
                .when()
                .post("/api/orders")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("createOrder"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION),
                        preprocessRequest(),
                        preprocessResponse(),
                        requestFields(
                            fieldWithPath("userId").type(NUMBER).description("사용자 ID"),
                            fieldWithPath("items").type(ARRAY).description("주문 상품 목록"),
                            fieldWithPath("items[].productOptionId").type(NUMBER).description("상품 옵션 ID"),
                            fieldWithPath("items[].quantity").type(NUMBER).description("수량"),
                            fieldWithPath("issuedCouponId").type(OBJECT).description("발급된 쿠폰 ID").optional(),
                            fieldWithPath("payMethod").type(STRING).description("결제 수단")
                        ),
                        responseFields(
                            fieldsWithBasic(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("주문 ID"),
                                fieldWithPath("data.status").type(STRING).description("주문 상태")
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }
} 