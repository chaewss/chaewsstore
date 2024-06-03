package com.chaewsstore.apis.admin.service;

import static com.chaewsstore.common.exception.ExceptionConstants.NOT_FOUND_ADMIN;

import com.chaewsstore.common.security.SecurityUtil;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.admin.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminDetailService implements UserDetailsService {

    private final AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminService.readByUsername(username).map(this::createUser)
            .orElseThrow(() -> NOT_FOUND_ADMIN);
    }

    private User createUser(Admin admin) {
        String role = admin.getRole().getKey();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        return new User(admin.getUsername(), admin.getPassword(),
            List.of(grantedAuthority));
    }
}
