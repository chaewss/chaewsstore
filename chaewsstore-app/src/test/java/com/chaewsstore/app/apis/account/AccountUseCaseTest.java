package com.chaewsstore.app.apis.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.apis.account.usecase.AccountUseCase;
import com.chaewsstore.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountErrorCode;
import com.chaewsstore.core.domain.account.AccountService;
import com.globalutils.exception.DuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountUseCaseTest {

    @InjectMocks
    private AccountUseCase accountUseCase;

    @Mock
    private AccountService accountService;

    @Mock
    private PasswordEncoderHelper passwordEncoderHelper;

    @Test
    @DisplayName("회원을 생성한다")
    void succeed_to_sign_in() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");
        String encodedPassword = "encodedPassword";

        // given
        given(accountService.existsByUsername(any())).willReturn(false);
        given(accountService.existsByNickname(any())).willReturn(false);
        given(passwordEncoderHelper.encodePassword(any())).willReturn(encodedPassword);

        given(accountService.create(any())).willReturn(account);

        // when
        accountUseCase.signup(request);

        // then
        then(accountService).should(times(1)).existsByUsername(any());
        then(accountService).should(times(1)).existsByNickname(any());
        then(passwordEncoderHelper).should(times(1)).encodePassword(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_account_username_is_duplicate() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

        // given
        given(accountService.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountUseCase.signup(request));

        // then
        then(accountService).should(times(1)).existsByUsername(any());
        assertEquals(AccountErrorCode.ACCOUNT_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_account_but_nickname_is_duplicate() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

        // given
        given(accountService.existsByUsername(any())).willReturn(false);
        given(accountService.existsByNickname(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountUseCase.signup(request));

        // then
        then(accountService).should(times(1)).existsByUsername(any());
        then(accountService).should(times(1)).existsByNickname(any());
        assertEquals(AccountErrorCode.NICKNAME_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void succeed_to_check_username() {
        // given
        given(accountService.existsByUsername(any())).willReturn(false);

        // when
        accountUseCase.checkUsername(any());

        // then
        then(accountService).should(times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_username_is_duplicate() {
        // given
        given(accountService.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountUseCase.checkUsername(any()));

        // then
        then(accountService).should(times(1)).existsByUsername(any());
        assertEquals(AccountErrorCode.ACCOUNT_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("닉네임 중복을 확인한다")
    void succeed_to_check_nickname() {
        // mocking
        given(accountService.existsByNickname(any())).willReturn(false);

        // when
        accountUseCase.checkNickname(account.getNickname());

        // then
        verify(accountService, times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_nickname_is_duplicate() {
        // mocking
        given(accountService.existsByNickname(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountUseCase.checkNickname(any()));

        // then
        verify(accountService, times(1)).existsByNickname(any());
        assertEquals(AccountErrorCode.NICKNAME_DUPLICATION, result.getResponseCode());
    }


    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();
}
