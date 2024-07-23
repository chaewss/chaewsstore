package com.chaewsstore.app.apis.bid;

import static com.chaewsstore.app.ApiDocumentUtils.documentIdentifier;
import static com.chaewsstore.app.ApiDocumentUtils.getDocumentRequest;
import static com.chaewsstore.app.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.app.apis.bid.controller.BidController;
import com.chaewsstore.app.apis.bid.dto.CreateBidRequestDto;
import com.chaewsstore.app.apis.bid.dto.ReadProductBidResponseDto;
import com.chaewsstore.app.apis.bid.dto.TransactBidRequestDto;
import com.chaewsstore.app.apis.bid.dto.UpdateBidRequestDto;
import com.chaewsstore.app.apis.bid.usecase.BidUseCase;
import com.chaewsstore.core.domain.bid.Bid.BidType;
import com.chaewsstore.core.domain.bid.dto.ReadProductBidQueryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
    @DisplayName("상품의 입찰 목록을 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_bid_list_succeed() throws Exception {
        Slice<ReadProductBidResponseDto> response = new SliceImpl<>(getProductBidResponseWithParam());
        given(bidUseCase.readProductBidList(any(), any(), any())).willReturn(response);

        mockMvc.perform(get("/api/products/{productId}/bids", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.content.[].bidPrice").type(JsonFieldType.NUMBER).description("판매 희망가"),
                    fieldWithPath("data.content.[].transactionAt").type(JsonFieldType.STRING).description("거래일")
                )
            ));
    }

    @ParameterizedTest
    @EnumSource(value = BidType.class)
    @DisplayName("bidType param을 입력하고 상품의 입찰 목록을 조회하면 HTTP 200을 응답한다")
    void respond_200_when_read_product_bid_list_with_param_succeed(BidType bidType) throws Exception {
        Slice<ReadProductBidResponseDto> response = new SliceImpl<>(getProductBidResponse());
        given(bidUseCase.readProductBidList(any(), any(), any())).willReturn(response);

        mockMvc.perform(get("/api/products/{productId}/bids", 1)
                .param("bidType", String.valueOf(bidType))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("productId").description("상품 ID")
                ),
                queryParameters(parameterWithName("bidType").description("입찰 타입(BUY / SELL)")),
                relaxedResponseFields(
                    fieldWithPath("data.content.[].bidPrice").type(JsonFieldType.NUMBER).description("판매 희망가"),
                    fieldWithPath("data.content.[].quantity").type(JsonFieldType.NUMBER).description("수량")
                )
            ));
    }

    @Test
    @DisplayName("판매 입찰 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_sell_bid_succeed() throws Exception {
        CreateBidRequestDto request = new CreateBidRequestDto(39000);

        mockMvc.perform(post("/api/products/{productId}/sell", 1)
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
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매 희망가")
                )
            ));
    }

    @Test
    @DisplayName("판매 입찰을 처리하고 관련된 구매 입찰 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_transact_sell_bid_and_create_buy_bid_succeed() throws Exception {
        TransactBidRequestDto request = new TransactBidRequestDto(1L, 39000);

        mockMvc.perform(post("/api/bids/buy-now")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매 희망가")
                )
            ));
    }

    @Test
    @DisplayName("구매 입찰 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_create_buy_bid_succeed() throws Exception {
        CreateBidRequestDto request = new CreateBidRequestDto(39000);

        mockMvc.perform(post("/api/products/{productId}/buy", 1)
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
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("구매 희망가")
                )
            ));
    }

    @Test
    @DisplayName("구매 입찰을 처리하고 관련된 판매 입찰 생성에 성공하면 HTTP 201을 응답한다")
    void respond_201_when_transact_buy_bid_and_create_sell_bid_succeed() throws Exception {
        TransactBidRequestDto request = new TransactBidRequestDto(1L, 39000);

        mockMvc.perform(post("/api/bids/sell-now")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 ID"),
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("판매 희망가")
                )
            ));
    }

    @Test
    @DisplayName("구매자가 입찰 상품 금액 입금에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_deposit_bid_succeed() throws Exception {
        mockMvc.perform(patch("/api/bids/deposit/{bidId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("bidId").description("입찰 ID")
                )
            ));
    }

    @Test
    @DisplayName("입찰 수정에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_update_bid_succeed() throws Exception {
        UpdateBidRequestDto request = new UpdateBidRequestDto(40000);

        mockMvc.perform(put("/api/bids/{bidId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("bidId").description("입찰 ID")
                ),
                requestFields(
                    fieldWithPath("price").type(JsonFieldType.NUMBER).description("수정할 희망가")
                )
            ));
    }

    @Test
    @DisplayName("입찰 삭제에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_delete_bid_succeed() throws Exception {
        mockMvc.perform(delete("/api/bids/{bidId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print())
            .andDo(document(documentIdentifier,
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("bidId").description("입찰 ID")
                )
            ));
    }

    private List<ReadProductBidResponseDto> getProductBidResponse() {
        ReadProductBidQueryDto queryDto1 = new ReadProductBidQueryDto(7000, 1L);
        ReadProductBidQueryDto queryDto2 = new ReadProductBidQueryDto(8000, 3L);
        ReadProductBidQueryDto queryDto3 = new ReadProductBidQueryDto(1000, 1L);
        return List.of(ReadProductBidResponseDto.from(queryDto1),
            ReadProductBidResponseDto.from(queryDto2), ReadProductBidResponseDto.from(queryDto3));
    }

    private List<ReadProductBidResponseDto> getProductBidResponseWithParam() {
        ReadProductBidQueryDto queryDto1 = new ReadProductBidQueryDto(7000, LocalDateTime.now());
        ReadProductBidQueryDto queryDto2 = new ReadProductBidQueryDto(8000, LocalDateTime.now().minusDays(1));
        ReadProductBidQueryDto queryDto3 = new ReadProductBidQueryDto(1000, LocalDateTime.now().minusDays(5));
        return List.of(ReadProductBidResponseDto.from(queryDto1),
            ReadProductBidResponseDto.from(queryDto2), ReadProductBidResponseDto.from(queryDto3));
    }
}
