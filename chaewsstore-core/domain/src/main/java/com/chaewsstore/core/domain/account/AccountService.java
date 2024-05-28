package com.chaewsstore.core.domain.account;

import com.globalutils.annotation.DomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DomainService
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Optional<Account> readByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Boolean existsByNickname(String nickname) {
        return accountRepository.existsByNickname(nickname);
    }
}
