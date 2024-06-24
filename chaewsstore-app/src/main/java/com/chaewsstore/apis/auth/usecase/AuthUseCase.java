package com.chaewsstore.apis.auth.usecase;

import static com.chaewsstore.common.exception.ExceptionConstants.INVALID_PASSWORD;
import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_USER;

import com.chaewsstore.apis.auth.dto.LoginRequestDto;
import com.chaewsstore.apis.auth.dto.LoginResponseDto;
import com.chaewsstore.apis.auth.dto.LogoutRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenRequestDto;
import com.chaewsstore.apis.auth.dto.ReissueTokenResponseDto;
import com.chaewsstore.apis.auth.helper.JwtAuthHelper;
import com.chaewsstore.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserService;
import com.chaewsstore.core.infra.jwt.Jwts;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.NotFoundException;
import com.globalutils.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class AuthUseCase {

    private final JwtAuthHelper jwtAuthHelper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoderHelper passwordEncoderHelper;

    /**
     * 로그인
     *
     * @param request 로그인 요청 정보
     * @return 사용자 id, 토큰 정보
     * @throws UnauthorizedException 비밀번호가 일치하지 않는 경우
     * @throws NotFoundException     해당하는 아이디를 가진 사용자가 없는 경우
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userService.readByUsername(request.email())
            .orElseThrow(() -> NOT_FOUND_USER);
        if (!passwordEncoderHelper.matches(request.password(), user.getPassword())) {
            throw INVALID_PASSWORD;
        }

        UsernamePasswordAuthenticationToken authenticationToken = request.toAuthentication();
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        Jwts token = jwtAuthHelper.generateTokensAndSave(user, authentication);

        return new LoginResponseDto(user.getId(), token);
    }

    /**
     * 토큰 재발급
     *
     * @param request 토큰 재발급 요청 정보
     * @return 사용자 id, 새로 발급된 토큰 정보
     * @throws NotFoundException 해당하는 아이디를 가진 사용자가 없는 경우
     */
    @Transactional
    public ReissueTokenResponseDto reissueToken(ReissueTokenRequestDto request) {
        String username = jwtAuthHelper.getSubject(request.accessToken());
        User user = userService.readByUsername(username)
            .orElseThrow(() -> NOT_FOUND_USER);

        Jwts token = jwtAuthHelper.reissueToken(user, request.refreshToken());

        return new ReissueTokenResponseDto(user.getId(), token);
    }

    /**
     * 로그아웃
     *
     * @param user    로그아웃할 사용자
     * @param request 로그아웃 요청 정보
     */
    public void logout(User user, LogoutRequestDto request) {
        jwtAuthHelper.removeRefreshToken(user, request.refreshToken());
    }
}
