package com.chaewsstore.core.domain.brand;

import com.chaewsstore.core.domain.BaseTimeEntity;
import com.globalutils.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Brand extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Builder
    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    @Generated
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Brand brand)) {
            return false;
        }
        return getId() != null && Objects.equals(getId(), brand.getId());
    }

    @Override
    @Generated
    public final int hashCode() {
        return Objects.hash(getId());
    }
}
