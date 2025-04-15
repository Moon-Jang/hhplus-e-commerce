package kr.hhplus.ecommerce.interfaces.product;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import org.springframework.http.HttpStatus;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder;

import kr.hhplus.ecommerce.application.product.ProductFacade;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.fieldsWithBasic;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.preprocessRequest;
import static kr.hhplus.ecommerce.common.ApiDocumentUtils.preprocessResponse;
import kr.hhplus.ecommerce.domain.product.ProductService;
import kr.hhplus.ecommerce.domain.product.ProductVoFixture;
import kr.hhplus.ecommerce.interfaces.ControllerTestContext;

public class ProductControllerTest extends ControllerTestContext {
    private static final String TAG = Tags.PRODUCT.tagName();

    @MockitoBean
    private ProductService productService;
    
    @MockitoBean
    private ProductFacade productFacade;

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProductTest {
        private static final String DESCRIPTION = Tags.PRODUCT.descriptionWith("상세 조회");

        @Test
        void 성공() {
            long productId = 1L;
            
            doReturn(new ProductVoFixture().create())
                .when(productService)
                .findById(anyLong());

            given()
                .when()
                .get("/v1/products/{productId}", productId)
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("getProduct"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION),
                        preprocessRequest(),
                        preprocessResponse(),
                        pathParameters(
                            parameterWithName("productId").description("상품 ID")
                        ),
                        responseFields(
                            fieldsWithBasic(
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data.name").type(STRING).description("상품 이름"),
                                fieldWithPath("data.price").type(NUMBER).description("상품 가격"),
                                fieldWithPath("data.description").type(STRING).description("상품 상세 설명"),
                                fieldWithPath("data.options").type(ARRAY).description("상품 옵션 목록"),
                                fieldWithPath("data.options[].id").type(NUMBER).description("옵션 ID"),
                                fieldWithPath("data.options[].name").type(STRING).description("옵션 이름"),
                                fieldWithPath("data.options[].stock").type(NUMBER).description("옵션 재고 수량")
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class GetAllProductsTest {
        private static final String DESCRIPTION = Tags.PRODUCT.descriptionWith("목록 조회");

        @Test
        void 성공() {
            doReturn(List.of(
                new ProductVoFixture().create()
            ))
                .when(productService)
                .findAll();

            given()
                .when()
                .get("/v1/products")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("getAllProducts"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION),
                        preprocessRequest(),
                        preprocessResponse(),
                        responseFields(
                            fieldsWithBasic(
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].id").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data[].name").type(STRING).description("상품 이름"),
                                fieldWithPath("data[].price").type(NUMBER).description("상품 가격"),
                                fieldWithPath("data[].options").type(ARRAY).description("상품 옵션 목록"),
                                fieldWithPath("data[].options[].id").type(NUMBER).description("옵션 ID"),
                                fieldWithPath("data[].options[].name").type(STRING).description("옵션 이름"),
                                fieldWithPath("data[].options[].stock").type(NUMBER).description("옵션 재고 수량")
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }
    
    @Nested
    @DisplayName("인기 상품 목록 조회")
    class GetTopSellingProductsTest {
        private static final String DESCRIPTION = Tags.PRODUCT.descriptionWith("인기 판매 상품 조회");

        @Test
        void 성공() {
            // given
            Integer limit = 5;
            doReturn(List.of(
                new ProductVoFixture().create(),
                new ProductVoFixture().setId(2L).setName("인기 상품 2").create(),
                new ProductVoFixture().setId(3L).setName("인기 상품 3").create()
            ))
                .when(productFacade)
                .findTopSellingProducts(anyInt());

            // when/then
            given()
                .param("limit", limit)
                .when()
                .get("/v1/products/top-selling")
                .then()
                .log().all()
                .apply(
                    document(
                        identifier("getTopSellingProducts"),
                        new ResourceSnippetParametersBuilder()
                            .tag(TAG)
                            .description(DESCRIPTION),
                        preprocessRequest(),
                        preprocessResponse(),
                        queryParameters(
                            parameterWithName("limit").description("조회할 인기 상품 개수")
                        ),
                        responseFields(
                            fieldsWithBasic(
                                fieldWithPath("data").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].id").type(NUMBER).description("상품 ID"),
                                fieldWithPath("data[].name").type(STRING).description("상품 이름"),
                                fieldWithPath("data[].price").type(NUMBER).description("상품 가격"),
                                fieldWithPath("data[].options").type(ARRAY).description("상품 옵션 목록"),
                                fieldWithPath("data[].options[].id").type(NUMBER).description("옵션 ID"),
                                fieldWithPath("data[].options[].name").type(STRING).description("옵션 이름"),
                                fieldWithPath("data[].options[].stock").type(NUMBER).description("옵션 재고 수량")
                            )
                        )
                    )
                )
                .status(HttpStatus.OK);
        }
    }
} 