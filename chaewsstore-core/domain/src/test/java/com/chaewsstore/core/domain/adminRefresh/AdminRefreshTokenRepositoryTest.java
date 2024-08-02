package com.chaewsstore.core.domain.adminRefresh;

import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.config.TestConfig;
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
class AdminRefreshTokenRepositoryTest {

    @Autowired
    AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        admin1 = Admin.builder()
            .username("admin1@gmail.com")
            .password("password123!")
            .name("관리자1")
            .isDeleted(false)
            .build();
        admin2 = Admin.builder()
            .username("admin2@gmail.com")
            .password("password123!")
            .name("관리자2")
            .isDeleted(false)
            .build();
        entityManager.persist(admin1);
        entityManager.persist(admin2);

        tokenValue = "testAdminRefreshToken";
        adminRefreshToken = AdminRefreshToken.create(admin1, tokenValue);
        entityManager.persist(adminRefreshToken);
    }

    @Test
    @DisplayName("토큰을 통해 관리자 리프레시 토큰을 조회한다")
    void succeed_to_find_admin_refresh_token_by_token() {
        Optional<AdminRefreshToken> foundAdminRefreshToken1 = adminRefreshTokenRepository.findByToken(tokenValue);
        Optional<AdminRefreshToken> foundAdminRefreshToken2 = adminRefreshTokenRepository.findByToken("nonExistentToken");

        assertThat(foundAdminRefreshToken1).contains(adminRefreshToken);
        assertThat(foundAdminRefreshToken2).isEmpty();
    }

    Admin admin1;
    Admin admin2;
    String tokenValue;
    AdminRefreshToken adminRefreshToken;
}
