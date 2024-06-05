package com.chaewsstore.app.apis.product;

import static com.chaewsstore.app.ApiDocumentUtils.documentIdentifier;
import static com.chaewsstore.app.ApiDocumentUtils.getDocumentRequest;
import static com.chaewsstore.app.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.apis.product.controller.ProductController;
import com.chaewsstore.apis.product.dto.CreateProductRequestDto;
import com.chaewsstore.apis.product.dto.ReadProductResponseDto;
import com.chaewsstore.apis.product.dto.UpdateProductRequestDto;
import com.chaewsstore.apis.product.usecase.ProductUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerUnitTest {

    @MockBean
    private ProductUseCase productUseCase;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(
        WebApplicationContext ctx, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .apply(sharedHttpSession())
            .build();
    }

    @Test
    @DisplayName("상품 목록을 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_list_succeed() throws Exception {
        Slice<ReadProductResponseDto> response = new SliceImpl<>(getProductResponse());
        given(productUseCase.readProductList(any())).willReturn(response);

        mockMvc.perform(get("/admin/products")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentResponse(),
                relaxedResponseFields(
                    fieldWithPath("data.content.[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("data.content.[].name").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("data.content.[].price").type(JsonFieldType.NUMBER).description("상품 출시 가격"),
                    fieldWithPath("data.content.[].brandName").type(JsonFieldType.STRING).description("브랜드명")
                )
            ));
    }

    @Test
    @DisplayName("상품 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_product_succeed() throws Exception {
        CreateProductRequestDto request = new CreateProductRequestDto("Adidas Superstar Core Black White", 139000, "Adidas");

        mockMvc.perform(post("/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 출시 가격"),
                    fieldWithPath("brandName").type(JsonFieldType.STRING).description("브랜드명")
                )
            ));
    }

    @Test
    @DisplayName("상품 수정에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_update_product_succeed() throws Exception {
        UpdateProductRequestDto request = new UpdateProductRequestDto("(J) Adidas Superstar Core Black White", 139000, "Adidas");

        mockMvc.perform(put("/admin/products/{productId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("name").type(JsonFieldType.STRING).description("상품명"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 출시 가격"),
                    fieldWithPath("brandName").type(JsonFieldType.STRING).description("브랜드명")
                )
            ));
    }

    private List<ReadProductResponseDto> getProductResponse() {
        ReadProductResponseDto dto1 = new ReadProductResponseDto(1L, "상품1", 600, "브랜드1");
        ReadProductResponseDto dto2 = new ReadProductResponseDto(2L, "상품2", 800, "브랜드2");
        ReadProductResponseDto dto3 = new ReadProductResponseDto(3L, "상품3", 1600, "브랜드1");
        return List.of(dto1, dto2, dto3);
    }
}
