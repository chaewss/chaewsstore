package com.chaewsstore.core.domain.refresh;

import static com.chaewsstore.core.domain.UserFixture.ANOTHER_USER;
import static com.chaewsstore.core.domain.UserFixture.USER;
import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.config.TestConfig;
import com.chaewsstore.core.domain.user.User;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        user = entityManager.merge(USER.getUser());
        anotherUser = entityManager.merge(ANOTHER_USER.getUser());

        tokenValue = "testAdminRefreshToken";
        refreshToken = RefreshToken.create(user, tokenValue);
        entityManager.persist(refreshToken);
    }

    @Test
    @DisplayName("토큰을 통해 사용자 리프레시 토큰을 조회한다")
    void succeed_to_find_refresh_token_by_token() {
        Optional<RefreshToken> foundRefreshToken1 = refreshTokenRepository.findByToken(tokenValue);
        Optional<RefreshToken> foundRefreshToken2 = refreshTokenRepository.findByToken("nonExistentToken");

        assertThat(foundRefreshToken1).contains(refreshToken);
        assertThat(foundRefreshToken2).isEmpty();
    }

    User user;
    User anotherUser;
    String tokenValue;
    RefreshToken refreshToken;
}
