package com.chaewsstore.apis.account.service;

import static com.chaewsstore.core.common.exception.ExceptionConstants.NOT_FOUND_ACCOUNT;

import com.chaewsstore.common.security.SecurityUtil;
import com.chaewsstore.core.common.exception.ExceptionConstants;
import com.chaewsstore.core.domain.account.Account;
import com.chaewsstore.core.domain.account.AccountRepository;
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
public class AccountDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).map(this::createUser)
            .orElseThrow(() -> NOT_FOUND_ACCOUNT);
    }

    @Transactional(readOnly = true)
    public Account getUserInfo() {
        return accountRepository.findByUsername(SecurityUtil.getCurrentUserName())
            .orElseThrow(() -> NOT_FOUND_ACCOUNT);
    }

    private User createUser(Account account) {
        String role = account.getRole().getKey();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        return new User(account.getUsername(), account.getPassword(),
            List.of(grantedAuthority));
    }
}
