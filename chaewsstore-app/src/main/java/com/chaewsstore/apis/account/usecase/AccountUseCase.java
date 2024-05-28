package com.chaewsstore.apis.account.usecase;

import static com.chaewsstore.core.common.exception.ExceptionConstants.ACCOUNT_DUPLICATION;
import static com.chaewsstore.core.common.exception.ExceptionConstants.NICKNAME_DUPLICATION;

import com.chaewsstore.apis.account.dto.AccountResponseDto;
import com.chaewsstore.apis.account.dto.SignupRequestDto;
import com.chaewsstore.apis.account.helper.PasswordEncoderHelper;
import com.chaewsstore.core.common.exception.DuplicateException;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountService;
import com.chaewsstore.core.domain.account.Role;
import com.globalutils.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class AccountUseCase {

    private final AccountService accountService;
    private final PasswordEncoderHelper passwordEncoderHelper;

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

        String encodedPassword = passwordEncoderHelper.encodePassword(request.password());
        Account account = request.toEntity(encodedPassword, Role.ASSOCIATE);
        accountService.create(account);

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
        if (Boolean.TRUE.equals(accountService.existsByUsername(username))) {
            throw ACCOUNT_DUPLICATION;
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
        if (Boolean.TRUE.equals(accountService.existsByNickname(nickname))) {
            throw NICKNAME_DUPLICATION;
        }
    }
}
