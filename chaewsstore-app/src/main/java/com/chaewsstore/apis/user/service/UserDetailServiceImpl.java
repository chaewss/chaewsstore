package com.chaewsstore.apis.user.service;

import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_USER;

import com.chaewsstore.core.domain.user.User;
import com.chaewsstore.core.infra.jwt.SecurityUtil;
import com.chaewsstore.core.domain.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.readByUsername(username).map(this::createUser)
            .orElseThrow(() -> NOT_FOUND_USER);
    }

    @Transactional(readOnly = true)
    public User getUserInfo() {
        return userService.readByUsername(SecurityUtil.getCurrentUserName())
            .orElseThrow(() -> NOT_FOUND_USER);
    }

    private org.springframework.security.core.userdetails.User createUser(User user) {
        String role = user.getRole().getKey();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
            List.of(grantedAuthority));
    }
}
