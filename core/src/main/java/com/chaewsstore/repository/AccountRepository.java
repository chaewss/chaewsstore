package com.chaewsstore.repository;

import com.chaewsstore.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Boolean existsByUsername(String username);

    Boolean existsByNickname(String nickname);

}
