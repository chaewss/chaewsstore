package com.chaewsstore.app.apis.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsstore.apis.account.service.AccountService;
import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountRepository;
import com.chaewsstore.core.common.exception.DuplicateException;
import com.chaewsstore.core.common.util.ResponseCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    Account account = Account.builder()
        .id(1L)
        .username("email@gmail.com")
        .password("aaaa1111!!")
        .nickname("닉네임")
        .build();

    @Test
    @DisplayName("회원을 생성한다")
    void succeed_to_sign_in() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);
        given(accountRepository.existsByNickname(any())).willReturn(false);

        given(accountRepository.save(any())).willReturn(account);

        // when
        accountService.signup(request);

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        then(accountRepository).should(times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_account_username_is_duplicate() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

        // given
        given(accountRepository.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.signup(request));

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        assertEquals(ResponseCode.ACCOUNT_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원가입시 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_create_account_but_nickname_is_duplicate() {
        SignupRequestDto request = new SignupRequestDto("email@gmail.com", "aaaa1111!!", "닉네임");

        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);
        given(accountRepository.existsByNickname(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.signup(request));

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        then(accountRepository).should(times(1)).existsByNickname(any());
        assertEquals(ResponseCode.NICKNAME_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이메일 중복을 확인한다")
    void succeed_to_check_username() {
        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);

        // when
        accountService.checkUsername(any());

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_username_is_duplicate() {
        // given
        given(accountRepository.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.checkUsername(any()));

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        assertEquals(ResponseCode.ACCOUNT_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("닉네임 중복을 확인한다")
    void succeed_to_check_nickname() {
        // mocking
        given(accountRepository.existsByNickname(any())).willReturn(false);

        // when
        accountService.checkNickname(account.getNickname());

        // then
        verify(accountRepository, times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 DuplicateException이 발생한다")
    void should_throw_DuplicateException_when_nickname_is_duplicate() {
        // mocking
        given(accountRepository.existsByNickname(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.checkNickname(any()));

        // then
        verify(accountRepository, times(1)).existsByNickname(any());
        assertEquals(ResponseCode.NICKNAME_DUPLICATION, result.getResponseCode());
    }
}
