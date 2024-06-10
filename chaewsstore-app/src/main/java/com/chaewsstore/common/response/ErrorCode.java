package com.chaewsstore.common.response;

import static com.globalutils.exception.StatusCode.BAD_REQUEST;
import static com.globalutils.exception.StatusCode.CONFLICT;
import static com.globalutils.exception.StatusCode.FORBIDDEN;
import static com.globalutils.exception.StatusCode.NOT_FOUND;
import static com.globalutils.exception.StatusCode.UNAUTHORIZED;

import com.globalutils.exception.BaseErrorCode;
import com.globalutils.exception.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALID_ERROR(BAD_REQUEST, "유효성 검사 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    ACCESS_DENIED(UNAUTHORIZED, "액세스가 거부되었습니다"),
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),
    WITHOUT_OWNERSHIP_REFRESH_TOKEN(UNAUTHORIZED, "소유권이 없는 리프레시 토큰입니다"),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    FORBIDDEN_BID(FORBIDDEN, "해당 입찰에 대한 권한이 없습니다"),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    NOT_FOUND_ACCOUNT(NOT_FOUND, "존재하지 않는 회원입니다"),
    NOT_FOUND_REFRESH_TOKEN(NOT_FOUND, "존재하지 않는 리프레시 토큰입니다"),
    NOT_FOUND_PRODUCT(NOT_FOUND, "존재하지 않는 상품입니다"),
    NOT_FOUND_BID(NOT_FOUND, "존재하지 않는 입찰입니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    ACCOUNT_DUPLICATION(CONFLICT, "중복된 아이디입니다"),
    NICKNAME_DUPLICATION(CONFLICT, "중복된 닉네임입니다"),
    DUPLICATION_BID(CONFLICT, "중복된 입찰입니다"),

    ADMIN_DUPLICATION(CONFLICT, "중복된 어드민 아이디입니다"),
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
