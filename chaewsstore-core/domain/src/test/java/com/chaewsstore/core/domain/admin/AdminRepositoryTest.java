package com.chaewsstore.core.domain.admin;

import static com.chaewsstore.core.domain.AdminFixture.ADMIN;
import static com.chaewsstore.core.domain.AdminFixture.ANOTHER_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@DisplayName("AdminRepository 클래스")
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        admin = entityManager.merge(ADMIN.getAdmin());
        anotherAdmin = entityManager.merge(ANOTHER_ADMIN.getAdmin());
    }

    @Test
    @DisplayName("이메일을 통해 관리자를 조회한다")
    void succeed_to_find_admin_by_username() {
        Optional<Admin> foundAdmin1 = adminRepository.findByUsername(admin.getUsername());
        Optional<Admin> foundAdmin2 = adminRepository.findByUsername(anotherAdmin.getUsername());

        assertThat(foundAdmin1).contains(admin);
        assertThat(foundAdmin2).contains(anotherAdmin);
    }

    @Test
    @DisplayName("이메일을 통해 관리자 존재 여부를 확인한다")
    void check_admin_exists_by_username() {
        Boolean exists1 = adminRepository.existsByUsername(admin.getUsername());
        Boolean exists2 = adminRepository.existsByUsername("관리자999");

        assertTrue(exists1);
        assertFalse(exists2);
    }

    Admin admin;
    Admin anotherAdmin;
}
