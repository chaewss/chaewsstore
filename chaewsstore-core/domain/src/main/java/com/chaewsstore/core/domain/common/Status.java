package com.chaewsstore.core.domain.common;

/**
 * LIVE: 입찰 중
 * IN_TRANSACTION: 거래 중
 * AUTHENTICATED: 검수 합격(100점)
 * ACCREDITED: 검수 부분 승인(95점)
 * AUTHENTICATED_FAILED: 검수 불합격
 * DELIVERING: 배송 중
 * DELIVERED: 배송 완료
 * FINISHED: 거래 완료
 * EXPIRED: 기한 만료
 * CANCELLED: 거래 취소
 */
public enum Status {

    LIVE,

    IN_TRANSACTION,

    AUTHENTICATED,

    ACCREDITED,

    AUTHENTICATED_FAILED,

    DELIVERING,

    DELIVERED,

    FINISHED,

    EXPIRED,

    CANCELLED
}
