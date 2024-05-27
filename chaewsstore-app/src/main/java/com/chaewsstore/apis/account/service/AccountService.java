package com.chaewsstore.apis.account.service;

import com.chaewsstore.apis.account.dto.AccountResponseDto;
import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountRepository;
import com.chaewsstore.core.domain.account.Role;
import com.chaewsstore.core.common.exception.DuplicateException;
import com.chaewsstore.core.common.exception.ExceptionConstants;
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
     * @throws DuplicateException 아이디 혹은 닉네임이 중복된 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto signup(SignupRequestDto request) {
        checkUsername(request.username());
        checkNickname(request.nickname());

        Account account = request.toEntity(passwordEncoder, Role.ASSOCIATE);
        accountRepository.save(account);

        return AccountResponseDto.from(account);
    }

    /**
     * 아이디 중복 체크를 한다.
     *
     * @param username 아이디
     * @throws DuplicateException 아이디가 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkUsername(String username) {
        if (Boolean.TRUE.equals(accountRepository.existsByUsername(username))) {
            throw ExceptionConstants.ACCOUNT_DUPLICATION;
        }
    }

    /**
     * 닉네임 중복 체크를 한다.
     *
     * @param nickname 닉네임
     * @throws DuplicateException 닉네임이 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        if (Boolean.TRUE.equals(accountRepository.existsByNickname(nickname))) {
            throw ExceptionConstants.NICKNAME_DUPLICATION;
        }
    }

}
