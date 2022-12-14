package com.chaewsscode.chaewsstore.auth.service;

import com.chaewsscode.chaewsstore.auth.controller.dto.AccountInfoResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.AccountResponseDto;
import com.chaewsscode.chaewsstore.auth.controller.dto.ResetPasswordRequestDto;
import com.chaewsscode.chaewsstore.auth.service.dto.SigninServiceDto;
import com.chaewsscode.chaewsstore.auth.service.dto.SignupServiceDto;
import com.chaewsscode.chaewsstore.config.TokenProvider;
import com.chaewsscode.chaewsstore.domain.Account;
import com.chaewsscode.chaewsstore.domain.RefreshToken;
import com.chaewsscode.chaewsstore.exception.DuplicateException;
import com.chaewsscode.chaewsstore.exception.ResourceNotFoundException;
import com.chaewsscode.chaewsstore.repository.AccountRepository;
import com.chaewsscode.chaewsstore.repository.RefreshTokenRepository;
import com.chaewsscode.chaewsstore.util.AccountUser;
import com.chaewsscode.chaewsstore.util.ResponseCode;
import com.chaewsscode.chaewsstore.util.SecurityUtil;
import com.chaewsscode.chaewsstore.util.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // ???????????? ?????? ?????? ?????? to @CurrentUser
    @Transactional(readOnly = true)
    public Account getUserInfo() {
        return accountRepository.findByUsername(SecurityUtil.getCurrentUserName())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
    }

    // ???????????? ?????? ?????? ??????
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return new AccountUser(account);
    }

    // ?????????
    @Transactional(rollbackFor = Exception.class)
    public AccountResponseDto signin(SigninServiceDto serviceDto) {
        // 1. Login ID/PW ??? ???????????? AuthenticationToken ?????? (????????? ??????)
        UsernamePasswordAuthenticationToken authenticationToken = serviceDto.toAuthentication();

        // 2. ?????? (????????? ???????????? ??????) ??? ??????????????? ??????
        //    authenticate ???????????? ????????? ??? ??? loadUserByUsername ???????????? ?????????
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        // 3. ?????? ????????? ???????????? JWT ?????? ??????
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken ??????
        Account account = accountRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));
        RefreshToken refreshToken = RefreshToken.builder()
            .account(account)
            .tokenValue(tokenDto.getRefreshToken())
            .build();

        refreshTokenRepository.save(refreshToken);

        // ????????? Authentication??? SecurityContext??? ??????
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. ?????? ?????? ?????? ?????? ?????? ??????
        AccountResponseDto accountResponseDto = AccountResponseDto.of(getUserInfo());
        accountResponseDto.setTokenDto(tokenDto);
        return accountResponseDto;
    }

    // ????????????
    @Transactional(rollbackFor = Exception.class)
    public void signout(String refreshToken) {
        // refresh token ??????
        RefreshToken deleteRefreshToken = refreshTokenRepository.findByTokenValue(refreshToken)
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.REFRESH_TOKEN_NOT_FOUND));
        refreshTokenRepository.delete(deleteRefreshToken);
    }

    @Transactional(rollbackFor = Exception.class)
    public void signup(SignupServiceDto serviceDto) {
        // ????????? ?????? ??????
        if (accountRepository.existsByUsername(serviceDto.getUsername())) {
            throw new DuplicateException(ResponseCode.ACCOUNT_DUPLICATION);
        }

        // ?????? ??????
        Account account = serviceDto.toAccount(passwordEncoder);
        accountRepository.save(account);
    }

    public AccountInfoResponseDto readAccountInfo(Account account) {
        return AccountInfoResponseDto.of(account);
    }

    public void resetPassword(ResetPasswordRequestDto request) {
        Account account = accountRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.ACCOUNT_NOT_FOUND));

        account.setPassword(passwordEncoder.encode(request.getPassword()));
    }

}
