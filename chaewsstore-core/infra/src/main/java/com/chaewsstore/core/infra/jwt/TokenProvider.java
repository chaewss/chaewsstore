package com.chaewsstore.core.infra.jwt;

import static com.chaewsstore.core.infra.exception.JwtErrorCode.EMPTY_ACCESS_TOKEN;
import static com.chaewsstore.core.infra.exception.JwtErrorCode.EXPIRED_TOKEN;
import static com.chaewsstore.core.infra.exception.JwtErrorCode.ILLEGAL_TOKEN;
import static com.chaewsstore.core.infra.exception.JwtErrorCode.INVALID_SIGNATURE;
import static com.chaewsstore.core.infra.exception.JwtErrorCode.MALFORMED_TOKEN;
import static com.chaewsstore.core.infra.exception.JwtErrorCode.UNSUPPORTED_TOKEN;
import static com.chaewsstore.core.infra.jwt.AuthConstants.ACCESS_TOKEN_TTL_MILLISECOND;
import static com.chaewsstore.core.infra.jwt.AuthConstants.BEARER_TYPE;
import static com.chaewsstore.core.infra.jwt.AuthConstants.REFRESH_TOKEN_TTL_MILLISECOND;
import static com.chaewsstore.core.infra.jwt.AuthConstants.ROLE_KEY;

import com.chaewsstore.core.infra.exception.JwtErrorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TokenProvider {

    private final SecretKey secretKey;

    public TokenProvider(@Value("${jwt.secret}") final String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 헤더로부터 토큰 추출
     *
     * @param authHeader 인증 헤더
     * @return 추출된 토큰
     * @throws JwtException 토큰이 없는 경우
     */
    public String extractToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_TYPE)) {
            return authHeader.substring(7);
        }
        throw new JwtErrorException(EMPTY_ACCESS_TOKEN);
    }

    /**
     * 인증된 사용자의 새로운 액세스 토큰 생성
     *
     * @param authentication 사용자의 인증 정보
     * @return 액세스 토큰
     */
    public String generateAccessToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .subject(authentication.getName())
            .claim(ROLE_KEY, authentication.getAuthorities())
            .issuedAt(new Date(now))
            .expiration(new Date(now + ACCESS_TOKEN_TTL_MILLISECOND))
            .signWith(secretKey)
            .compact();
    }

    /**
     * 새로운 리프레시 토큰 생성
     *
     * @return 리프레시 토큰
     */
    public String generateRefreshToken() {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .issuedAt(new Date(now))
            .expiration(new Date(now + REFRESH_TOKEN_TTL_MILLISECOND))
            .signWith(secretKey)
            .compact();
    }

    /**
     * 토큰을 기반으로 사용자의 인증 정보 추출
     *
     * @param token 토큰
     * @return 인증 정보
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String email = claims.getSubject();
        GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(ROLE_KEY).toString());

        return new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
    }

    public String getSubjectFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 토큰에서 클레임들을 추출하여 Claims 객체로 반환
     *
     * @param token 토큰
     * @return 사용자 정보
     * @throws JwtException 유효하지 않은 JWT 토큰인 경우
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (SignatureException e) {
            throw new JwtErrorException(INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new JwtErrorException(MALFORMED_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new JwtErrorException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtErrorException(UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtErrorException(ILLEGAL_TOKEN);
        }
    }
}
