package com.chaewsstore.admin.apis.admin.usecase;

import static com.chaewsstore.admin.common.exception.ExceptionConstants.ADMIN_DUPLICATION;

import com.chaewsstore.admin.apis.admin.dto.AdminResponseDto;
import com.chaewsstore.admin.apis.admin.dto.AdminSignupRequestDto;
import com.chaewsstore.admin.common.helper.PasswordEncoderHelper;
import com.chaewsstore.core.domain.admin.Admin;
import com.chaewsstore.core.domain.admin.AdminService;
import com.globalutils.annotation.UseCase;
import com.globalutils.exception.DuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@UseCase
public class AdminUseCase {

    private final AdminService adminService;
    private final PasswordEncoderHelper passwordEncoderHelper;

    /**
     * 어드민을 등록한다.
     *
     * @param request 회원가입할 어드민의 정보
     * @return 회원가입 처리된 어드민 정보
     * @throws DuplicateException 아이디가 중복된 경우
     */
    @Transactional
    public AdminResponseDto signup(AdminSignupRequestDto request) {
        checkUsername(request.username());

        String encodedPassword = passwordEncoderHelper.encodePassword(request.password());
        Admin admin = request.toEntity(encodedPassword);
        adminService.create(admin);

        return AdminResponseDto.from(admin);
    }

    /**
     * 아이디 중복 체크를 한다.
     *
     * @param username 아이디
     * @throws DuplicateException 아이디가 중복된 경우
     */
    @Transactional(readOnly = true)
    public void checkUsername(String username) {
        if (Boolean.TRUE.equals(adminService.existsByUsername(username))) {
            throw ADMIN_DUPLICATION;
        }
    }
}
