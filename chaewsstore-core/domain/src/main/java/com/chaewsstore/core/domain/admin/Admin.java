package com.chaewsstore.core.domain.admin;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.chaewsstore.core.domain.user.Role;
import com.globalutils.annotation.Generated;
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

@Getter
@SQLDelete(sql = "UPDATE admin SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Boolean isDeleted;

    @Builder
    public Admin(Long id, String username, String password, String name, Boolean isDeleted) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = Role.ADMIN;
        this.isDeleted = isDeleted;
    }

    public static Admin create(String username, String password, String name) {
        return Admin.builder()
            .username(username)
            .password(password)
            .name(name)
            .isDeleted(false)
            .build();
    }

    @Override
    @Generated
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Admin admin)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), admin.getId());
    }

    @Override
    @Generated
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
