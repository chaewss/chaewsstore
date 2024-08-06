package com.chaewsstore.core.domain.adminRefresh;

import static com.chaewsstore.core.domain.AdminFixture.ADMIN;
import static com.chaewsstore.core.domain.AdminFixture.ANOTHER_ADMIN;
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
    private AdminRefreshTokenRepository adminRefreshTokenRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        admin = entityManager.merge(ADMIN.getAdmin());
        anotherAdmin = entityManager.merge(ANOTHER_ADMIN.getAdmin());

        tokenValue = "testAdminRefreshToken";
        adminRefreshToken = AdminRefreshToken.create(admin, tokenValue);
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

    Admin admin;
    Admin anotherAdmin;
    String tokenValue;
    AdminRefreshToken adminRefreshToken;
}
