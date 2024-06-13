package com.chaewsstore.apis.user.usecase;

import static com.chaewsstore.common.exception.ExceptionConstants.USER_DUPLICATION;
import static com.chaewsstore.common.exception.ExceptionConstants.NICKNAME_DUPLICATION;

import com.chaewsstore.apis.user.dto.UserResponseDto;
import com.chaewsstore.apis.user.dto.SignupRequestDto;
import com.chaewsstore.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.DuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class UserUseCase {

    private final UserService userService;
    private final PasswordEncoderHelper passwordEncoderHelper;

    /**
     * 사용자를 등록한다.
     *
     * @param request 회원가입할 사용자의 정보
     * @return 회원가입 처리된 사용자 정보
     * @throws DuplicateException 아이디 혹은 닉네임이 중복된 경우
     */
    @Transactional(rollbackFor = Exception.class)
    public UserResponseDto signup(SignupRequestDto request) {
        checkUsername(request.username());
        checkNickname(request.nickname());

        String encodedPassword = passwordEncoderHelper.encodePassword(request.password());
        User user = request.toEntity(encodedPassword, Role.ASSOCIATE);
        userService.create(user);

        return UserResponseDto.from(user);
    }

    /**
     * 아이디 중복 체크를 한다.
     *
     * @param username 아이디
     * @throws DuplicateException 아이디가 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkUsername(String username) {
        if (Boolean.TRUE.equals(userService.existsByUsername(username))) {
            throw USER_DUPLICATION;
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
        if (Boolean.TRUE.equals(userService.existsByNickname(nickname))) {
            throw NICKNAME_DUPLICATION;
        }
    }
}
