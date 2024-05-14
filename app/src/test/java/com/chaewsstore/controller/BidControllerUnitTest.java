package com.chaewsstore.controller;

import static com.chaewsstore.ApiDocumentUtils.documentIdentifier;
import static com.chaewsstore.ApiDocumentUtils.getDocumentRequest;
import static com.chaewsstore.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.auth.TokenProvider;
import com.chaewsstore.config.RequestMatcherHolder;
import com.chaewsstore.dto.bid.CreateBidRequestDto;
import com.chaewsstore.service.BidService;
import com.chaewsstore.util.LoginAccountArgumentResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(BidController.class)
class BidControllerUnitTest {

    @MockBean
    RequestMatcherHolder requestMatcherHolder;

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    LoginAccountArgumentResolver loginAccountArgumentResolver;

    @MockBean
    private BidService bidService;

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
    @DisplayName("입찰 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_bid_succeed() throws Exception {
        CreateBidRequestDto request = new CreateBidRequestDto(39000);

        mockMvc.perform(post("/api/products/{productId}/bids", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                requestFields(
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("상품 입찰 가격")
                )
            ));
    }
}
