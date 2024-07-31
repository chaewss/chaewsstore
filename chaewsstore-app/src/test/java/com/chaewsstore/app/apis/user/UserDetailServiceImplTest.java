package com.chaewsstore.app.apis.user;

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

    @Test
    @DisplayName("userDetails를 반환한다")
    void succeed_to_load_userByUsername() {
        given(userService.readByUsername(any())).willReturn(Optional.of(user));

        UserDetails result = userDetailServiceImpl.loadUserByUsername(username);

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

    @Test
    @DisplayName("현재 사용자 정보를 반환한다")
    void succeed_to_getUserInfo() {
        given(userService.readByUsername(any())).willReturn(Optional.of(user));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password,
            List.of(new SimpleGrantedAuthority("ROLE_ASSOCIATE")));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        User result = userDetailServiceImpl.getUserInfo();

        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        then(userService).should(times(1)).readByUsername(any());
    }

    @Test
    @DisplayName("현재 사용자를 찾지 못했을 때 NotFoundException이 발생한다")
    void should_throw_NotFoundException_when_getUserInfo_but_user_not_found() {
        given(userService.readByUsername(any())).willReturn(Optional.empty());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            "whoAreYou@gmail.com", password, List.of(new SimpleGrantedAuthority("ROLE_ASSOCIATE")));
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        NotFoundException result = assertThrows(NotFoundException.class, () -> {
            userDetailServiceImpl.getUserInfo();
        });

        then(userService).should(times(1)).readByUsername(any());
        assertEquals(UserErrorCode.NOT_FOUND_USER, result.getResponseCode());
    }

    private final String username = "email@gmail.com";
    private final String password = "aaaa1111!!";
    private final User user = User.builder()
        .id(1L)
        .username(username)
        .password(password)
        .nickname("닉네임")
        .role(Role.ASSOCIATE)
        .build();
}
