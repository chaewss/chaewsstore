package com.chaewsstore.jwt;

import static com.chaewsstore.core.infra.jwt.AuthConstants.ROLE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.chaewsstore.core.infra.exception.JwtErrorCode;
import com.chaewsstore.core.infra.exception.JwtErrorException;
import com.chaewsstore.core.infra.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    private TokenProvider tokenProvider;

    private String secretStr = "helloThisIsChaewsstoreSecretKeyAndItNeedsToBeLongerThan256Bits";

    private SecretKey key;

    @BeforeEach
    public void setUp() {
        tokenProvider = new TokenProvider(secretStr);
        byte[] keyBytes = Decoders.BASE64.decode(secretStr);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    @DisplayName("시크릿키가 존재하는지 확인한다")
    void should_exist_secret_key() {
        assertThat(key).isNotNull();
    }

    @Test
    @DisplayName("토큰 추출에 성공한다")
    void should_extract_token_from_header() {
        String token = "testToken";
        String authHeader = "Bearer " + token;
        String extractedToken = tokenProvider.extractToken(authHeader);
        assertThat(extractedToken).isEqualTo(token);
    }

    @Test
    @DisplayName("헤더에 토큰이 없을 경우 토큰 추출에 실패한다")
    void should_throw_exception_when_token_is_missing() {
        String authHeader = "";
        assertThrows(JwtErrorException.class, () -> tokenProvider.extractToken(authHeader));
    }

    @Test
    @DisplayName("헤더가 Bearer로 시작하지 않을 경우 토큰 추출에 실패한다")
    void should_throw_exception_when_token_does_not_start_with_bearer() {
        String token = "testToken";
        String authHeader = "Digest " + token;
        assertThrows(JwtErrorException.class, () -> tokenProvider.extractToken(authHeader));
    }

    @Test
    @DisplayName("액세스 토큰을 생성한다")
    void should_generate_access_token() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "admin@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        String token = tokenProvider.generateAccessToken(authentication);
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertThat(claims.getSubject()).isEqualTo("admin@example.com");
        assertThat(claims.get(ROLE_KEY)).hasToString("[{authority=ROLE_ADMIN}]");
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("리프레시 토큰을 생성한다")
    void should_generate_refresh_token() {
        String token = tokenProvider.generateRefreshToken();
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        assertThat(claims.getId()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("토큰에서 인증 정보를 추출한다")
    void should_get_authentication_from_token() {
        String token = Jwts.builder()
            .subject("testAdmin")
            .claim(ROLE_KEY, "ROLE_ADMIN")
            .signWith(key)
            .compact();

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(authentication.getName()).isEqualTo("testAdmin");
        assertThat(authentication.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .contains("ROLE_ADMIN");
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 인증 정보 추출에 실패한다")
    void should_throw_exception_for_invalid_token() {
        String invalidToken = "invalidToken";
        assertThrows(JwtErrorException.class, () -> tokenProvider.getAuthentication(invalidToken));
    }

    @Test
    @DisplayName("토큰에서 주체를 추출한다")
    void should_get_subject_from_token() {
        String subject = "user";
        String token = Jwts.builder()
            .subject(subject)
            .signWith(key)
            .compact();

        String result = tokenProvider.getSubjectFromToken(token);

        assertThat(result).isEqualTo(subject);
    }

    @Test
    @DisplayName("토큰에서 클레임을 추출한다")
    void should_get_claims_from_token() {
        String token = Jwts.builder()
            .subject("testAdmin")
            .signWith(key)
            .compact();

        Claims claims = tokenProvider.getClaimsFromToken(token);

        assertThat(claims.getSubject()).isEqualTo("testAdmin");
    }

    @Test
    @DisplayName("유효하지 않은 토큰일 경우 토큰에서 클레임을 추출하는데 실패한다")
    void should_throw_exception_for_malformed_jwt() {
        String malformedToken = "malformedToken";

        JwtErrorException result = assertThrows(JwtErrorException.class,
            () -> tokenProvider.getClaimsFromToken(malformedToken));

        assertEquals(JwtErrorCode.MALFORMED_TOKEN, result.getErrorCode());
    }

    @Test
    @DisplayName("잘못된 JWT 시그니처일 경우 토큰에서 클레임을 추출하는데 실패한다")
    void should_throw_exception_for_invalid_jwt_signature() {
        String token = Jwts.builder()
            .subject("testAdmin")
            .signWith(Keys.hmacShaKeyFor("wrongsecretkeywrongsecretkeywrongsecretkey".getBytes()))
            .compact();

        JwtErrorException result = assertThrows(JwtErrorException.class,
            () -> tokenProvider.getClaimsFromToken(token));

        assertEquals(JwtErrorCode.INVALID_SIGNATURE, result.getErrorCode());
    }

    @Test
    @DisplayName("만료된 JWT 토큰일 경우 토큰에서 클레임을 추출하는데 실패한다")
    void should_throw_exception_for_expired_jwt() {
        String token = Jwts.builder()
            .subject("user")
            .expiration(new Date(System.currentTimeMillis() - 1000))
            .signWith(key)
            .compact();

        JwtErrorException result = assertThrows(JwtErrorException.class,
            () -> tokenProvider.getClaimsFromToken(token));

        assertEquals(JwtErrorCode.EXPIRED_TOKEN, result.getErrorCode());
    }

    @Test
    @DisplayName("지원되지 않는 JWT 토큰일 경우 토큰에서 클레임을 추출하는데 실패한다")
    void should_throw_exception_for_unsupported_jwt() {
        String token = Jwts.builder()
            .signWith(key)
            .compact();

        JwtErrorException result = assertThrows(JwtErrorException.class,
            () -> tokenProvider.getClaimsFromToken(token));

        assertEquals(JwtErrorCode.UNSUPPORTED_TOKEN, result.getErrorCode());
    }

    @Test
    @DisplayName("잘못된 JWT 토큰일 경우 토큰에서 클레임을 추출하는데 실패한다")
    void should_throw_exception_for_illegal_jwt() {
        String illegalToken = "";

        JwtErrorException result = assertThrows(JwtErrorException.class,
            () -> tokenProvider.getClaimsFromToken(illegalToken));

        assertEquals(JwtErrorCode.ILLEGAL_TOKEN, result.getErrorCode());
    }
}
