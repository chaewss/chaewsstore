package com.chaewsstore.core.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = User.builder().build();
    }

    @Test
    @DisplayName("사용자를 생성한다")
    void should_create_user() {
        given(userRepository.save(user)).willReturn(user);

        User result = userService.create(user);

        then(userRepository).should(times(1)).save(user);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("아이디로 낙관적 락을 사용하여 사용자를 읽어온다")
    void should_read_user_by_id_with_optimistic_lock() {
        Long userId = 1L;
        given(userRepository.findByIdWithOptimisticLock(userId)).willReturn(Optional.of(user));

        Optional<User> result = userService.readByIdWithOptimisticLock(userId);

        then(userRepository).should(times(1)).findByIdWithOptimisticLock(userId);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("유저명을 사용하여 사용자를 읽어온다")
    void should_read_user_by_username() {
        String username = "testUsername";
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        Optional<User> result = userService.readByUsername(username);

        then(userRepository).should(times(1)).findByUsername(username);
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("사용자 이름이 존재하는지 확인한다")
    void should_check_if_username_exists() {
        String username = "testUsername";
        given(userRepository.existsByUsername(username)).willReturn(true);

        Boolean result = userService.existsByUsername(username);

        then(userRepository).should(times(1)).existsByUsername(username);
        assertTrue(result);
    }

    @Test
    @DisplayName("닉네임이 존재하는지 확인한다")
    void should_check_if_nickname_exists() {
        String nickname = "testNickname";
        given(userRepository.existsByNickname(nickname)).willReturn(true);

        Boolean result = userService.existsByNickname(nickname);

        then(userRepository).should(times(1)).existsByNickname(nickname);
        assertTrue(result);
    }

    User user;
}
