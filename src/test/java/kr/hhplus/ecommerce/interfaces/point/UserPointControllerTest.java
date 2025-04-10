package kr.hhplus.ecommerce.interfaces.point;

import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;
import kr.hhplus.ecommerce.domain.point.UserPointService;
import kr.hhplus.ecommerce.domain.point.UserPointVo;
import kr.hhplus.ecommerce.interfaces.ControllerTestContext;
import kr.hhplus.ecommerce.interfaces.UserPointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class UserPointControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.POINT.tagName();

    @MockitoBean
    private UserPointService userPointService;

    @Nested
    @DisplayName("포인트 충전")
    class chargeTest {
        private static final String DESCRIPTION = Tags.POINT.descriptionWith("충전");

        @Test
        void success() {
            var request = new UserPointRequest.Charge(1L, 1000);

            doReturn(new UserPointVo(1L, 1L, 1000))
                .when(userPointService)
                .charge(any());

            given()
                .body(request)
                .when()
                .post("/v1/points/charge")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("charge"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION),
                        preprocessRequest(),
                        preprocessResponse(),
                        requestFields(
                            fieldWithPath("userId").type(NUMBER).description("사용자 ID"),
                            fieldWithPath("amount").type(NUMBER).description("충전할 포인트")
                        ),
                        responseFields(
                            fieldsWithBasic(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("포인트 ID"),
                                fieldWithPath("data.userId").type(NUMBER).description("사용자 ID"),
                                fieldWithPath("data.amount").type(NUMBER).description("충전된 포인트")
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }
}