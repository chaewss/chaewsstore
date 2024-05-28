package com.chaewsstore.core.domain.refresh;

import org.springframework.data.jpa.repository.JpaRepository;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
