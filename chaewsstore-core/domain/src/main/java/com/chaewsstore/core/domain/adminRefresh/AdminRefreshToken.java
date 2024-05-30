package com.chaewsstore.core.domain.adminRefresh;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.admin.Admin;
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
public class AdminRefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @NotNull
    private String token;

    private AdminRefreshToken(Admin admin, String token) {
        this.admin = admin;
        this.token = token;
    }

    public static AdminRefreshToken create(Admin admin, String token) {
        return new AdminRefreshToken(admin, token);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminRefreshToken adminRefreshToken)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), adminRefreshToken.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
