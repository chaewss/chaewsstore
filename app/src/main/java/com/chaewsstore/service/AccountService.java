package com.chaewsstore.service;

import static com.chaewsstore.exception.ExceptionConstants.ACCOUNT_DUPLICATION;
import static com.chaewsstore.exception.ExceptionConstants.NICKNAME_DUPLICATION;

import com.chaewsstore.dto.account.AccountResponseDto;
import com.chaewsstore.dto.account.SignupRequestDto;
import com.chaewsstore.entity.Account;
import com.chaewsstore.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자를 등록한다.
     *
     * @param request 회원가입할 사용자의 정보
     * @return 회원가입 처리된 사용자 정보
     * @throws com.chaewsstore.exception.DuplicateException 아이디 혹은 닉네임이 중복된 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto signup(SignupRequestDto request) {
        checkUsername(request.username());
        checkNickname(request.nickname());

        Account account = request.toEntity(passwordEncoder);
        accountRepository.save(account);

        return AccountResponseDto.from(account);
    }

    /**
     * 아이디 중복 체크를 한다.
     *
     * @param username 아이디
     * @throws com.chaewsstore.exception.DuplicateException 아이디가 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkUsername(String username) {
        if (Boolean.TRUE.equals(accountRepository.existsByUsername(username))) {
            throw ACCOUNT_DUPLICATION;
        }
    }

    /**
     * 닉네임 중복 체크를 한다.
     *
     * @param nickname 닉네임
     * @throws com.chaewsstore.exception.DuplicateException 닉네임이 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (Boolean.TRUE.equals(accountRepository.existsByNickname(nickname))) {
            throw NICKNAME_DUPLICATION;
        }
    }

}
