package com.chaewsstore.app.apis.user;

import static com.chaewsstore.core.domain.UserFixture.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsstore.app.apis.user.dto.SignupRequestDto;
import com.chaewsstore.app.apis.user.usecase.UserUseCase;
import com.chaewsstore.app.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserErrorCode;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.exception.DuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("UserUseCase 클래스")
@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @InjectMocks
    private UserUseCase userUseCase;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoderHelper passwordEncoderHelper;

    @Nested
    @DisplayName("signup 메서드는")
    class sign_up {

        @Test
        @DisplayName("회원가입에 성공하면 생성된 회원을 반환한다")
        void succeed_to_sign_up() {
            SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");
            String encodedPassword = "encodedPassword";

            // given
            given(userService.existsByUsername(any())).willReturn(false);
            given(userService.existsByNickname(any())).willReturn(false);
            given(passwordEncoderHelper.encodePassword(any())).willReturn(encodedPassword);

            given(userService.create(any())).willReturn(user);

            // when
            userUseCase.signup(request);

            // then
            then(userService).should(times(1)).existsByUsername(any());
            then(userService).should(times(1)).existsByNickname(any());
            then(passwordEncoderHelper).should(times(1)).encodePassword(any());
        }

        @Test
        @DisplayName("생성할 이메일이 이미 존재하면 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_create_user_username_is_duplicate() {
            SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

            // given
            given(userService.existsByUsername(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> userUseCase.signup(request));

            // then
            then(userService).should(times(1)).existsByUsername(any());
            assertEquals(UserErrorCode.USER_DUPLICATION, result.getResponseCode());
        }

        @Test
        @DisplayName("생성할 닉네임이 이미 존재하면 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_create_user_but_nickname_is_duplicate() {
            SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

            // given
            given(userService.existsByUsername(any())).willReturn(false);
            given(userService.existsByNickname(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> userUseCase.signup(request));

            // then
            then(userService).should(times(1)).existsByUsername(any());
            then(userService).should(times(1)).existsByNickname(any());
            assertEquals(UserErrorCode.NICKNAME_DUPLICATION, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("checkUsername 메서드는")
    class check_username {

        @Test
        @DisplayName("이메일이 중복되지 않을 경우 예외를 발생시키지 않는다")
        void succeed_to_check_username() {
            // given
            given(userService.existsByUsername(any())).willReturn(false);

            // when
            userUseCase.checkUsername(any());

            // then
            then(userService).should(times(1)).existsByUsername(any());
        }

        @Test
        @DisplayName("이미 이메일이 존재하는 경우 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_username_is_duplicate() {
            // given
            given(userService.existsByUsername(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> userUseCase.checkUsername(any()));

            // then
            then(userService).should(times(1)).existsByUsername(any());
            assertEquals(UserErrorCode.USER_DUPLICATION, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("checkNickname 메서드")
    class check_nickname {

        @Test
        @DisplayName("닉네임이 중복되지 않을 경우 예외를 발생시키지 않는다")
        void succeed_to_check_nickname() {
            // mocking
            given(userService.existsByNickname(any())).willReturn(false);

            // when
            userUseCase.checkNickname(user.getNickname());

            // then
            verify(userService, times(1)).existsByNickname(any());
        }

        @Test
        @DisplayName("이미 닉네임이 존재하는 경우 DuplicateException이 발생한다")
        void should_throw_DuplicateException_when_nickname_is_duplicate() {
            // mocking
            given(userService.existsByNickname(any())).willReturn(true);

            // when
            DuplicateException result = assertThrows(DuplicateException.class,
                () -> userUseCase.checkNickname(any()));

            // then
            verify(userService, times(1)).existsByNickname(any());
            assertEquals(UserErrorCode.NICKNAME_DUPLICATION, result.getResponseCode());
        }
    }

    User user = USER.getUser();
}
