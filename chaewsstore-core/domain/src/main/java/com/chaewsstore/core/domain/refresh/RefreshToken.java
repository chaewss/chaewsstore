package com.chaewsstore.core.domain.refresh;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.user.User;
import com.globalutils.annotation.Generated;
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
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private String token;

    private RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public static RefreshToken create(User user, String token) {
        return new RefreshToken(user, token);
    }

    @Override
    @Generated
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
    @Generated
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
