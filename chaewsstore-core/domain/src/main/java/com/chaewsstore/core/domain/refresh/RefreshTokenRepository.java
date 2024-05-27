package com.chaewsstore.core.domain.refresh;

import com.chaewsstore.core.domain.account.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAccount(Account account);
}
