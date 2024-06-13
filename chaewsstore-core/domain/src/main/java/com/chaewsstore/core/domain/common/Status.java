package com.chaewsstore.core.domain.common;

/**
 * LIVE: 입찰 중
 * IN_TRANSACTION: 거래 중
 * DELIVERING: 배송 중
 * DELIVERED: 배송 완료
 * FINISHED: 거래 완료
 * EXPIRED: 기한 만료
 * CANCELLED: 거래 취소
 */
public enum Status {

    LIVE,

    IN_TRANSACTION,

    DELIVERING,

    DELIVERED,

    FINISHED,

    EXPIRED,

    CANCELLED
}
