package com.chaewsstore.admin.apis.auth;

import static com.chaewsstore.common.exception.ExceptionConstants.INVALID_PASSWORD;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_ADMIN;
import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.chaewsstore.admin.ApiDocumentUtils;
import com.chaewsstore.apis.auth.controller.AuthController;
import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.apis.auth.usecase.AuthUseCase;
import com.chaewsstore.core.infra.jwt.Jwts;
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
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @MockBean
    private AuthUseCase authUseCase;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    String accessToken = "Bearer (accessToken)";
    String refreshToken = "(refreshToken)";

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .apply(sharedHttpSession())
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @Test
    @DisplayName("로그인에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_login_succeed() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("admin@gmail.com", "password1!");
        Jwts token = Jwts.of(accessToken, refreshToken, BEARER_TYPE);
        LoginResponseDto responseDto = new LoginResponseDto(1L, token);

        given(authUseCase.login(any())).willReturn(responseDto);

        mockMvc.perform(post("/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                    fieldWithPath("data.token.accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("data.token.refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰"),
                    fieldWithPath("data.token.grantType").type(JsonFieldType.STRING).description("토큰 타입")
                )
            ));
    }

    @Test
    @DisplayName("해당 이메일을 가진 계정이 존재하지 않으면 로그인 API 호출시 HTTP 404를 응답한다")
    void respond_404_when_login_but_user_does_not_exist() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("admin@gmail.com", "password1!");

        given(authUseCase.login(any())).willThrow(NOT_FOUND_ADMIN);

        mockMvc.perform(post("/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound())
            .andDo(print());
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 로그인 API 호출시 HTTP 401을 응답한다")
    void respond_401_when_password_is_not_correct() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("admin@gmail.com", "password1!");

        given(authUseCase.login(any())).willThrow(INVALID_PASSWORD);

        mockMvc.perform(post("/admin/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isUnauthorized())
            .andDo(print());
    }

    @Test
    @DisplayName("토큰 재발급에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_reissue_token_succeed() throws Exception {
        String newAccessToken = "Bearer (new accessToken)";
        String newRefreshToken = "(new refreshToken)";
        ReissueTokenRequestDto request = new ReissueTokenRequestDto(accessToken, refreshToken);
        Jwts token = Jwts.of(newAccessToken, newRefreshToken, BEARER_TYPE);
        ReissueTokenResponseDto response = new ReissueTokenResponseDto(1L, token);

        given(authUseCase.reissueToken(any())).willReturn(response);

        mockMvc.perform(post("/admin/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                    fieldWithPath("accessToken").type(JsonFieldType.STRING).description("액세스 토큰"),
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.adminId").type(JsonFieldType.NUMBER).description("관리자 ID"),
                    fieldWithPath("data.token.accessToken").type(JsonFieldType.STRING).description("새로 발급된 액세스 토큰"),
                    fieldWithPath("data.token.refreshToken").type(JsonFieldType.STRING).description("새로 발급된 리프레시 토큰"),
                    fieldWithPath("data.token.grantType").type(JsonFieldType.STRING).description("토큰 타입")
                )
            ));
    }

    @Test
    @DisplayName("로그아웃에 성공하면 HTTP 200을 응답한다")
    void respond_200_when_logout_succeed() throws Exception {
        LogoutRequestDto request = new LogoutRequestDto(refreshToken);
        mockMvc.perform(post("/admin/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                requestFields(
                    fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("리프레시 토큰")
                )
            ));
    }
}
