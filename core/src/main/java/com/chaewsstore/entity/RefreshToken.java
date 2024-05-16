package com.chaewsstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    private String token;

    private RefreshToken(Account account, String token) {
        this.account = account;
        this.token = token;
    }

    public static RefreshToken create(Account account, String token) {
        return new RefreshToken(account, token);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefreshToken refreshToken)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), refreshToken.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
