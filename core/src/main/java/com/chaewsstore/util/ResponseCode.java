package com.chaewsstore.util;

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
    LOGIN_SUCCESS(OK, "로그인 성공"),
    CHECK_USERNAME_SUCCESS(OK, "사용가능한 아이디입니다"),
    CHECK_NICKNAME_SUCCESS(OK, "사용가능한 닉네임입니다"),

    READ_PRODUCT_BID_SUCCESS(OK, "상품 입찰 목록 조회 성공"),
    UPDATE_BID_SUCCESS(OK, "입찰 수정 성공"),

    /* 201 CREATED : 요청 성공, 자원 생성 */
    SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
    CREATE_BID_SUCCESS(CREATED, "입찰 생성 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_TOKEN(UNAUTHORIZED, "토큰 관련 오류입니다"),
    ACCESS_DENIED(UNAUTHORIZED, "액세스가 거부되었습니다"),
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_BID(FORBIDDEN, "해당 입찰에 대한 권한이 없습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_ACCOUNT(NOT_FOUND, "존재하지 않는 회원입니다"),
    NOT_FOUND_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다"),
    NOT_FOUND_BID(NOT_FOUND, "존재하지 않는 입찰입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ACCOUNT_DUPLICATION(CONFLICT, "중복된 아이디입니다"),
    NICKNAME_DUPLICATION(CONFLICT, "중복된 닉네임입니다"),
    DUPLICATION_BID(CONFLICT, "중복된 입찰입니다");

    private final HttpStatus httpStatus;
    private final String detail;

}
