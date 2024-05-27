package com.chaewsstore.app.apis.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.apis.account.controller.AccountController;
import com.chaewsstore.apis.account.dto.AccountResponseDto;
import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.apis.account.service.AccountService;
import com.chaewsstore.app.ApiDocumentUtils;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerUnitTest {

    @MockBean
    private AccountService accountService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup(WebApplicationContext ctx, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .apply(documentationConfiguration(restDocumentationContextProvider))
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .apply(sharedHttpSession())
            .build();
    }

    @Test
    @DisplayName("회원가입에 성공하면 201을 응답한다")
    void respond_201_when_sign_up_succeed() throws Exception {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");
        AccountResponseDto response = new AccountResponseDto(1L, "email@gmail.com", "닉네임");

        given(accountService.signup(any())).willReturn(response);

        mockMvc.perform(post("/api/accounts/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                    fieldWithPath("username").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                    fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자의 고유한 ID값"),
                    fieldWithPath("data.username").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임")
                )
            ));
    }

    @Test
    @DisplayName("해당 이메일로 가입된 계정이 존재하지 않으면 이메일 중복 검사에서 200을 응답한다")
    void respond_200_when_username_does_not_exist() throws Exception {
        final String username = "aaaa1111!!";

        mockMvc.perform(get("/api/accounts/check-username/{username}/exists", username).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("username").description("확인하고자 하는 이메일")
                ))
            );
    }

    @Test
    @DisplayName("해당 닉네임으로 가입된 계정이 존재하지 않으면 닉네임 중복 검사에서 200을 응답한다")
    void respond_200_when_nickname_does_not_exist() throws Exception {
        final String nickname = "닉네임";

        mockMvc.perform(get("/api/accounts/check-nickname/{nickname}/exists", nickname).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("nickname").description("확인하고자 하는 닉네임")
                ))
            );
    }
}
