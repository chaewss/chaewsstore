package com.chaewsstore.repository;

import static com.chaewsstore.entity.Role.ASSOCIATE;
import static org.assertj.core.api.Assertions.assertThat;

import com.chaewsstore.entity.Account;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void initData() {
        account = Account.create("abc@naver.com", "password1!", "닉네임", ASSOCIATE);
        accountRepository.save(account);
    }

    @Test
    @DisplayName("이메일로 사용자를 찾는다")
    void succeed_to_find_user_by_email() {
        Optional<Account> foundAccount = accountRepository.findByUsername(account.getUsername());

        assertThat(foundAccount).isPresent();
    }

    Account account;
}
