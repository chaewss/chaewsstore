package com.chaewsstore.app.apis.user;

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

    @Test
    @DisplayName("회원을 생성한다")
    void succeed_to_sign_in() {
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
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateException이 발생한다")
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
    @DisplayName("이미 존재하는 닉네임이면 회원가입시 DuplicateException이 발생한다")
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

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void succeed_to_check_username() {
        // given
        given(userService.existsByUsername(any())).willReturn(false);

        // when
        userUseCase.checkUsername(any());

        // then
        then(userService).should(times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 DuplicateException이 발생한다")
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

    @Test
    @DisplayName("닉네임 중복을 확인한다")
    void succeed_to_check_nickname() {
        // mocking
        given(userService.existsByNickname(any())).willReturn(false);

        // when
        userUseCase.checkNickname(user.getNickname());

        // then
        verify(userService, times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 DuplicateException이 발생한다")
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


    User user = User.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();
}
