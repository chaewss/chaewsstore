package com.chaewsstore.core.domain.admin;

import static com.chaewsstore.core.domain.AdminFixture.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        admin = ADMIN.getAdmin();
    }

    @Test
    @DisplayName("관리자 계정을 생성한다")
    void should_create_admin() {
        given(adminRepository.save(admin)).willReturn(admin);

        Admin result = adminService.create(admin);

        then(adminRepository).should(times(1)).save(admin);
        assertEquals(admin, result);
    }

    @Test
    @DisplayName("사용자 이름으로 관리자 계정을 조회한다")
    void should_read_admin_by_username() {
        String username = "adminUsername";
        given(adminRepository.findByUsername(username)).willReturn(Optional.of(admin));

        Optional<Admin> result = adminService.readByUsername(username);

        then(adminRepository).should(times(1)).findByUsername(username);
        assertTrue(result.isPresent());
        assertEquals(admin, result.get());
    }

    @Test
    @DisplayName("사용자 이름으로 관리자 계정의 존재 여부를 확인한다")
    void should_check_if_admin_exists_by_username() {
        String username = "adminUsername";
        given(adminRepository.existsByUsername(username)).willReturn(true);

        Boolean result = adminService.existsByUsername(username);

        then(adminRepository).should(times(1)).existsByUsername(username);
        assertTrue(result);
    }

    Admin admin;
}
