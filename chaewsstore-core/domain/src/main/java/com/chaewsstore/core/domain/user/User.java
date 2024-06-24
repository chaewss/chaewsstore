package com.chaewsstore.core.domain.user;

import static com.chaewsstore.core.domain.user.UserErrorCode.INSUFFICIENT_BALANCE;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.globalutils.exception.BadRequestException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@SQLDelete(sql = "UPDATE user SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User extends BaseTimeEntity {

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

    @Column(nullable = false)
    private Long account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Version
    private Long version;

    private Boolean isDeleted;

    @Builder
    public User(Long id, String username, String password, String nickname, Long account, Role role,
        Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.account = account;
        this.role = role;
        this.isDeleted = isDeleted;
    }

    public static User create(String username, String password, String nickname, Role role) {
        return User.builder()
            .username(username)
            .password(password)
            .nickname(nickname)
            .account(0L)
            .role(role)
            .isDeleted(false)
            .build();
    }

    public void deposit(Long amount) {
        this.account += amount;
    }

    public void withdraw(Long amount) {
        validateSufficientBalance(amount);
        this.account -= amount;
    }

    private void validateSufficientBalance(Long money) {
        if (this.account < money) {
            throw new BadRequestException(INSUFFICIENT_BALANCE);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
