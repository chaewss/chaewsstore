package com.chaewsstore.auth;

import static com.chaewsstore.auth.AuthConstants.ACCESS_TOKEN_TTL_MILLISECOND;
import static com.chaewsstore.auth.AuthConstants.BEARER_TYPE;
import static com.chaewsstore.auth.AuthConstants.REFRESH_TOKEN_TTL_MILLISECOND;
import static com.chaewsstore.auth.AuthConstants.ROLE_KEY;

import com.chaewsstore.entity.Account;
import com.chaewsstore.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenProvider {

    private final SecretKey secretKey;
    private final AccountRepository accountRepository;

    public TokenProvider(@Value("${jwt.secret}") final String secretKey,
        AccountRepository accountRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accountRepository = accountRepository;
    }

    public String createAccessToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .subject(authentication.getName())
            .claim(ROLE_KEY, authentication.getAuthorities())
            .issuedAt(new Date(now))
            .expiration(new Date(now + ACCESS_TOKEN_TTL_MILLISECOND))
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken() {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .issuedAt(new Date(now))
            .expiration(new Date(now + REFRESH_TOKEN_TTL_MILLISECOND))
            .signWith(secretKey)
            .compact();
    }

    /**
     * HTTP 요청 헤더에서 토큰 추출
     *
     * @param request HTTP 요청
     * @return 추출된 토큰 문자열
     * @throws JwtException 토큰이 없는 경우
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        throw new JwtException("토큰이 없습니다");
    }

    /**
     * 토큰을 기반으로 클레임 정보 추출
     *
     * @param accessToken 토큰
     * @return 추출된 클레임 정보 {@link Claims}
     */
    public Claims parseToken(String accessToken) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(accessToken)
                .getPayload();
        } catch (SecurityException e) {
            throw new JwtException("잘못된 JWT 시그니처입니다");
        } catch (MalformedJwtException e) {
            throw new JwtException("유효하지 않은 JWT 토큰입니다");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT 토큰이 잘못되었습니다");
        }
    }

    /**
     * 토큰을 기반으로 사용자의 인증 정보 추출
     *
     * @param accessToken 토큰
     * @return 인증 정보 {@link Authentication}
     */
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseToken(accessToken);
        String email = claims.getSubject();
        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(ROLE_KEY).toString());

        return new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
    }

}
