package com.chaewsstore.app.apis.user;

import static com.chaewsstore.core.domain.UserFixture.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.app.apis.user.service.UserDetailServiceImpl;
import com.chaewsstore.core.domain.user.Role;
import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.domain.user.UserErrorCode;
import com.chaewsstore.core.domain.user.UserService;
import com.globalutils.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@DisplayName("UserDetailServiceImpl 클래스")
@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @InjectMocks
    private UserDetailServiceImpl userDetailServiceImpl;

    @Mock
    private UserService userService;

    @Nested
    @DisplayName("loadUserByUsername 메서드는")
    class load_user_by_username {

        @Test
        @DisplayName("이메일로 userDetails를 조회해 반환한다")
        void succeed_to_load_user_by_username() {
            given(userService.readByUsername(any())).willReturn(Optional.of(user));

            UserDetails result = userDetailServiceImpl.loadUserByUsername(any());

            assertNotNull(result);
            assertEquals(user.getUsername(), result.getUsername());
            assertEquals(user.getPassword(), result.getPassword());
            then(userService).should(times(1)).readByUsername(any());
        }

        @Test
        @DisplayName("사용자를 찾지 못했을 때 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_loadUserByUsername_but_user_not_found() {
            given(userService.readByUsername(any())).willReturn(Optional.empty());

            NotFoundException result = assertThrows(NotFoundException.class, () -> {
                userDetailServiceImpl.loadUserByUsername("whoAreYou");
            });

            then(userService).should(times(1)).readByUsername(any());
            assertEquals(UserErrorCode.NOT_FOUND_USER, result.getResponseCode());
        }
    }

    @Nested
    @DisplayName("getUserInfo 메서드는")
    class get_user_info {

        String username = "user@gmail.com";
        String password = "password1!";

        @Test
        @DisplayName("이메일로 현재 사용자 정보를 조회해 반환한다")
        void succeed_to_get_user_info() {
            given(userService.readByUsername(any())).willReturn(Optional.of(user));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(username,
                password, List.of(new SimpleGrantedAuthority("ROLE_ASSOCIATE")));
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            User result = userDetailServiceImpl.getUserInfo();

            assertEquals(user.getUsername(), result.getUsername());
            assertEquals(user.getPassword(), result.getPassword());
            then(userService).should(times(1)).readByUsername(any());
        }

        @Test
        @DisplayName("해당하는 이메일을 가진 사용자가 없으면 NotFoundException이 발생한다")
        void should_throw_NotFoundException_when_get_user_info_but_user_not_found() {
            given(userService.readByUsername(any())).willReturn(Optional.empty());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                "whoAreYou@gmail.com", password,
                List.of(new SimpleGrantedAuthority("ROLE_ASSOCIATE")));
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            NotFoundException result = assertThrows(NotFoundException.class, () -> {
                userDetailServiceImpl.getUserInfo();
            });

            then(userService).should(times(1)).readByUsername(any());
            assertEquals(UserErrorCode.NOT_FOUND_USER, result.getResponseCode());
        }
    }

    private final User user = USER.getUser();
}
