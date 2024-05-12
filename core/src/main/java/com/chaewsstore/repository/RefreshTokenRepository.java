package com.chaewsstore.repository;

import com.chaewsstore.entity.Account;
import com.chaewsstore.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByAccount(Account account);
}
