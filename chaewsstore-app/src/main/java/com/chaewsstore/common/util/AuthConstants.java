package com.chaewsstore.common.util;

public interface AuthConstants {

    String BEARER_TYPE = "Bearer";

    String ROLE_KEY = "role";

    long ACCESS_TOKEN_TTL_MILLISECOND = 1_000L * 60 * 30;               // 30분

    long REFRESH_TOKEN_TTL_MILLISECOND = 1_000L * 60 * 60 * 24 * 30;    // 30일

}
