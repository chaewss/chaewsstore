package com.chaewsstore.service;

import static com.chaewsstore.exception.ExceptionConstants.NOT_FOUND_ACCOUNT;

import com.chaewsstore.entity.Account;
import com.chaewsstore.repository.AccountRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username).map(this::createUser)
            .orElseThrow(() -> NOT_FOUND_ACCOUNT);
    }

    private User createUser(Account account) {
        String role = account.getRole().getKey();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        return new User(account.getUsername(), account.getPassword(),
            List.of(grantedAuthority));
    }
}
