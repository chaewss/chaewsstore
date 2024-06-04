package com.chaewsstore.core.domain.adminRefresh;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRefreshTokenRepository extends JpaRepository<AdminRefreshToken, Long> {

    Optional<AdminRefreshToken> findByToken(String token);
}
