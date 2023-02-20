package com.chaewsstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.chaewsstore.controller.dto.SignupRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.exception.DuplicateException;
import com.chaewsstore.repository.AccountRepository;
import com.chaewsstore.util.ResponseCode;
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
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        SignupRequestDto signupRequest = SignupRequestDto.builder()
            .username("email@gmail.com")
            .password("aaaa1111!!")
            .nickname("닉네임")
            .build();

        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);
        given(accountRepository.existsByNickname(any())).willReturn(false);

        given(accountRepository.save(any())).willReturn(account);

        // when
        accountService.signup(signupRequest);

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        then(accountRepository).should(times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 아이디")
    void signupFailUsernameDuplication() {
        SignupRequestDto signupRequest = SignupRequestDto.builder()
            .username("email@gmail.com")
            .password("aaaa1111!!")
            .nickname("닉네임")
            .build();

        // given
        given(accountRepository.existsByUsername(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.signup(signupRequest));

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        assertEquals(ResponseCode.ACCOUNT_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임")
    void signupFailNicknameDuplication() {
        SignupRequestDto signupRequest = SignupRequestDto.builder()
            .username("email@gmail.com")
            .password("aaaa1111!!")
            .nickname("닉네임")
            .build();

        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);
        given(accountRepository.existsByNickname(any())).willReturn(true);

        // when
        DuplicateException result = assertThrows(DuplicateException.class,
            () -> accountService.signup(signupRequest));

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
        then(accountRepository).should(times(1)).existsByNickname(any());
        assertEquals(ResponseCode.NICKNAME_DUPLICATION, result.getResponseCode());
    }

    @Test
    @DisplayName("이메일 중복 확인 성공")
    void checkUsernameSuccess() {
        // given
        given(accountRepository.existsByUsername(any())).willReturn(false);

        // when
        accountService.checkUsername(any());

        // then
        then(accountRepository).should(times(1)).existsByUsername(any());
    }

    @Test
    @DisplayName("이메일 중복 확인 실패 - 중복된 아이디")
    void checkUsernameFailDuplication() {
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
    @DisplayName("닉네임 중복 확인 성공")
    void checkNicknameSuccess() {
        // mocking
        given(accountRepository.existsByNickname(any())).willReturn(false);

        // when
        accountService.checkNickname(account.getNickname());

        // then
        verify(accountRepository, times(1)).existsByNickname(any());
    }

    @Test
    @DisplayName("닉네임 중복 확인 실패 - 중복된 닉네임")
    void checkNicknameFailDuplication() {
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
