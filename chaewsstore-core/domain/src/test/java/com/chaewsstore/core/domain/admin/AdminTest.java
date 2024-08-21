package com.chaewsstore.core.domain.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Admin 클래스")
class AdminTest {

    @Test
    @DisplayName("create 메서드는 관리자 생성 시, 초기 값들을 올바르게 설정한다")
    void should_create_admin() {
        // given
        String username = "adminUsername";
        String password = "adminPassword";
        String name = "adminName";

        // when
        Admin admin = Admin.create(username, password, name);

        // then
        assertNotNull(admin);
        assertEquals(username, admin.getUsername());
        assertEquals(password, admin.getPassword());
        assertEquals(name, admin.getName());
        assertFalse(admin.getIsDeleted());
    }
}
