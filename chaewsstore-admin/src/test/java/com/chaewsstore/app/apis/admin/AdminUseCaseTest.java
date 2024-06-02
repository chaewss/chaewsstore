package com.chaewsstore.app.apis.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.apis.admin.helper.PasswordEncoderHelper;
import com.chaewsstore.apis.admin.usecase.AdminUseCase;
import com.chaewsstore.common.exception.DuplicateException;
import com.chaewsstore.common.response.ResponseCode;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.admin.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminUseCaseTest {

    @InjectMocks
    private AdminUseCase adminUseCase;

    @Mock
    private AdminService adminService;

    @Mock
    private PasswordEncoderHelper passwordEncoderHelper;

    @Test
    @DisplayName("회원을 생성한다")
    void succeed_to_sign_in() {
        AdminSignupRequestDto request = new AdminSignupRequestDto("admin@gmail.com", "aaaa1111!!", "어드민");
        String encodedPassword = "encodedPassword";

        // given
        given(adminService.existsByUsername(any())).willReturn(false);
        given(passwordEncoderHelper.encodePassword(any())).willReturn(encodedPassword);

        given(adminService.create(any())).willReturn(admin);

        // when
        adminUseCase.signup(request);

        // then
        then(adminService).should(times(1)).existsByUsername(any());
        then(passwordEncoderHelper).should(times(1)).encodePassword(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_account_username_is_duplicate() {
        AdminSignupRequestDto request = new AdminSignupRequestDto("admin@gmail.com", "aaaa1111!!", "어드민");

        // given
        given(adminService.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> adminUseCase.signup(request));

        // then
        then(adminService).should(times(1)).existsByUsername(any());
        assertEquals(ResponseCode.ADMIN_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void succeed_to_check_username() {
        // given
        given(adminService.existsByUsername(any())).willReturn(false);

        // when
        adminUseCase.checkUsername(any());

        // then
        then(adminService).should(times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_username_is_duplicate() {
        // given
        given(adminService.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> adminUseCase.checkUsername(any()));

        // then
        then(adminService).should(times(1)).existsByUsername(any());
        assertEquals(ResponseCode.ADMIN_DUPLICATION, result.getResponseCode());
    }

    Admin admin = Admin.builder()
        .id(1L)
        .username("admin@gmail.com")
        .password("aaaa1111!!")
        .name("어드민")
        .build();
}
