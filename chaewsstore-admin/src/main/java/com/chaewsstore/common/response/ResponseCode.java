package com.chaewsstore.common.response;

import static com.globalutils.exception.StatusCode.BAD_REQUEST;
import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.CREATED;
import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.OK;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseResponseCode;
import com.globalutils.exception.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode implements BaseResponseCode {

    /* 200 OK : 요청 성공 */
    LOGIN_SUCCESS(OK, "로그인 성공"),
    CHECK_USERNAME_SUCCESS(OK, "사용가능한 아이디입니다"),
    REISSUE_TOKEN_SUCCESS(OK, "토큰 재발급 성공"),
    LOGOUT_SUCCESS(OK, "로그아웃 성공"),

    READ_PRODUCTS_SUCCESS(OK, "상품 리스트 조회 성공"),
    UPDATE_PRODUCT_SUCCESS(OK, "상품 수정 성공"),
    DELETE_PRODUCT_SUCCESS(OK, "상품 삭제 성공"),

    /* 201 CREATED : 요청 성공, 자원 생성 */
    SIGNUP_SUCCESS(CREATED, "회원가입 성공"),
    CREATE_PRODUCT_SUCCESS(CREATED, "상품 생성 성공"),

    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_TOKEN(UNAUTHORIZED, "토큰 관련 오류입니다"),
    ACCESS_DENIED(UNAUTHORIZED, "액세스가 거부되었습니다"),
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "잘못된 리프레시 토큰입니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */


    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_ADMIN(NOT_FOUND, "존재하지 않는 어드민입니다"),
    NOT_FOUND_REFRESH_TOKEN(NOT_FOUND, "존재하지 않는 리프레시 토큰입니다"),
    NOT_FOUND_BRAND(NOT_FOUND, "존재하지 않는 브랜드입니다"),
    NOT_FOUND_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ADMIN_DUPLICATION(CONFLICT, "중복된 아이디입니다"),
    DUPLICATE_PRODUCT(CONFLICT, "중복된 상품입니다"),
    ;

    private final StatusCode statusCode;
    private final String detail;

    @Override
    public String statusCode() {
        return name();
    }

    @Override
    public String getExplainDetail() {
        return detail;
    }
}
