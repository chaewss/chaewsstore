package com.chaewsstore.entity;

import static com.chaewsstore.exception.ExceptionConstants.INVALID_PASSWORD;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@SQLDelete(sql = "UPDATE account SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Boolean isDeleted;

    @Builder
    public Account(Long id, String username, String password, String nickname, Role role,
        Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    public static Account create(String username, String password, String nickname, Role role) {
        return Account.builder()
            .username(username)
            .password(password)
            .nickname(nickname)
            .role(role)
            .isDeleted(false)
            .build();
    }

    public void validatePassword(String password, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(password, this.password)) {
            throw INVALID_PASSWORD;
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account account)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), account.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
