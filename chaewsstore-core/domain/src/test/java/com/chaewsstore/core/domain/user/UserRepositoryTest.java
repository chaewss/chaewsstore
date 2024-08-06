package com.chaewsstore.core.domain.user;

import static com.chaewsstore.core.domain.UserFixture.ANOTHER_USER;
import static com.chaewsstore.core.domain.UserFixture.USER;
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

@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        user = entityManager.merge(USER.getUser());
        anotherUser = entityManager.merge(ANOTHER_USER.getUser());
    }

    @Test
    @DisplayName("아이디를 통해 사용자를 조회한다")
    void succeed_to_find_user_by_id() {
        Optional<User> foundUser = userRepository.findByIdWithOptimisticLock(user.getId());

        assertThat(foundUser)
            .isPresent()
            .contains(user);
    }

    @Test
    @DisplayName("이메일으로 사용자를 조회한다")
    void succeed_to_find_user_by_username() {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        assertThat(foundUser)
            .isPresent()
            .contains(user);
    }

    @Test
    @DisplayName("이메일으로 사용자 존재 여부를 확인한다")
    void check_user_exists_by_username() {
        Boolean exists = userRepository.existsByUsername(user.getUsername());
        Boolean notExists = userRepository.existsByUsername("user999");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("닉네임으로 사용자 존재 여부를 확인한다")
    void check_user_exists_by_nickname() {
        Boolean exists = userRepository.existsByNickname(user.getNickname());
        Boolean notExists = userRepository.existsByNickname("nonExistentNickname");

        assertTrue(exists);
        assertFalse(notExists);
    }

    User user;
    User anotherUser;
}
