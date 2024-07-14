package com.chaewsstore.admin.apis.bid;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.admin.ApiDocumentUtils;
import com.chaewsstore.apis.bid.controller.BidController;
import com.chaewsstore.apis.bid.dto.InspectBidProductRequestDto;
import com.chaewsstore.apis.bid.usecase.BidUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(BidController.class)
class BidControllerUnitTest {

    @MockBean
    private BidUseCase bidUseCase;

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
    @DisplayName("입찰 상품 검수에 성공하면 200을 응답한다")
    void respond_200_when_inspect_bid_product_succeed() throws Exception {
        InspectBidProductRequestDto request = new InspectBidProductRequestDto(100);

        mockMvc.perform(patch("/admin/bids/inspect/{bidId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("bidId").description("입찰 ID")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("검수 점수")
                )
            ));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    @DisplayName("score 값이 0 미만 100 초과일 경우 입찰 상품 검수 API 호출시 400를 응답한다")
    void respond_400_when_inspect_bid_product_but_invalid_scores(int invalidScore) throws Exception {
        InspectBidProductRequestDto request = new InspectBidProductRequestDto(invalidScore);

        mockMvc.perform(patch("/admin/bids/inspect/{bidId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("bidId").description("입찰 ID")
                ),
                requestFields(
                    fieldWithPath("score").type(JsonFieldType.NUMBER).description("검수 점수")
                )
            ));
    }
}
