package com.chaewsstore.admin.apis.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.admin.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.admin.apis.admin.usecase.AdminUseCase;
import com.chaewsstore.admin.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.admin.AdminErrorCode;
import com.chaewsstore.core.domain.admin.AdminService;
import com.globalutils.exception.DuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("AdminUseCase 클래스")
@ExtendWith(MockitoExtension.class)
class AdminUseCaseTest {

    @InjectMocks
    private AdminUseCase adminUseCase;

    @Mock
    private AdminService adminService;

    @Mock
    private PasswordEncoderHelper passwordEncoderHelper;

    @Nested
    @DisplayName("signup 메서드는")
    class sign_up {

        @Test
        @DisplayName("회원가입에 성공하면 생성된 어드민을 반환한다")
        void succeed_to_sign_up() {
            AdminSignupRequestDto request = new AdminSignupRequestDto("admin@gmail.com",
                "aaaa1111!!", "어드민");
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
        @DisplayName("생성할 이메일이 이미 존재하면 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_create_user_username_is_duplicate() {
            AdminSignupRequestDto request = new AdminSignupRequestDto("admin@gmail.com",
                "aaaa1111!!", "어드민");

            // given
            given(adminService.existsByUsername(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> adminUseCase.signup(request));

            // then
            then(adminService).should(times(1)).existsByUsername(any());
            assertEquals(AdminErrorCode.ADMIN_DUPLICATION, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("checkUsername 메서드는")
    class check_username {
        @Test
        @DisplayName("이메일이 중복되지 않을 경우 예외를 발생시키지 않는다")
        void succeed_to_check_username() {
            // given
            given(adminService.existsByUsername(any())).willReturn(false);

            // when
            adminUseCase.checkUsername(any());

            // then
            then(adminService).should(times(1)).existsByUsername(any());
        }

        @Test
        @DisplayName("이미 이메일이 존재하는 경우 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_username_is_duplicate() {
            // given
            given(adminService.existsByUsername(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> adminUseCase.checkUsername(any()));

            // then
            then(adminService).should(times(1)).existsByUsername(any());
            assertEquals(AdminErrorCode.ADMIN_DUPLICATION, result.getResponseCode());
        }
    }

    Admin admin = Admin.builder()
        .id(1L)
        .username("admin@gmail.com")
        .password("aaaa1111!!")
        .name("어드민")
        .build();
}
