package com.chaewsstore.app.apis.admin;

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

import com.chaewsstore.apis.admin.controller.AdminController;
import com.chaewsstore.apis.admin.dto.AdminResponseDto;
import com.chaewsstore.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.apis.admin.usecase.AdminUseCase;
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
@WebMvcTest(AdminController.class)
class AdminControllerUnitTest {

    @MockBean
    private AdminUseCase adminUseCase;

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
        AdminSignupRequestDto request = new AdminSignupRequestDto("admin@gmail.com", "aaaa1111!!", "어드민");
        AdminResponseDto response = new AdminResponseDto(1L, "admin@gmail.com", "어드민");

        given(adminUseCase.signup(any())).willReturn(response);

        mockMvc.perform(post("/admin/signup")
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
                    fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                ),
                relaxedResponseFields(
                    fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("사용자의 고유한 ID값"),
                    fieldWithPath("data.username").type(JsonFieldType.STRING).description("이메일"),
                    fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름")
                )
            ));
    }

    @Test
    @DisplayName("해당 이메일로 가입된 계정이 존재하지 않으면 이메일 중복 검사에서 200을 응답한다")
    void respond_200_when_username_does_not_exist() throws Exception {
        final String username = "admin@gmail.com";

        mockMvc.perform(get("/admin/check-username/{username}/exists", username).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcRestDocumentation.document(ApiDocumentUtils.documentIdentifier,
                ApiDocumentUtils.getDocumentRequest(),
                ApiDocumentUtils.getDocumentResponse(),
                pathParameters(
                    parameterWithName("username").description("확인하고자 하는 이메일")
                ))
            );
    }
}
