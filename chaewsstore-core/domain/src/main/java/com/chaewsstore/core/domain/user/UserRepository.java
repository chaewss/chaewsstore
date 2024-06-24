package com.chaewsstore.core.domain.user;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface UserRepository extends JpaRepository<User, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query(value = "SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithOptimisticLock(@Param("id") Long id);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

}
