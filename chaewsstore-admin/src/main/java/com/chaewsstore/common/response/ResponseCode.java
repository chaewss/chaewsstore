package com.chaewsstore.common.response;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    /* 200 OK : 요청 성공 */
    CHECK_USERNAME_SUCCESS(OK, "사용가능한 아이디입니다"),

    /* 201 CREATED : 요청 성공, 자원 생성 */
    SIGNUP_SUCCESS(CREATED, "회원가입 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */


    /* 403 FORBIDDEN : 권한이 없는 사용자 */


    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */


    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ADMIN_DUPLICATION(CONFLICT, "중복된 아이디입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}
