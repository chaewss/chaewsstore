package com.chaewsstore.core.infra.jwt;

public class AuthConstants {

    public static final String BEARER_TYPE = "Bearer";

    public static final String ROLE_KEY = "role";

    public static final long ACCESS_TOKEN_TTL_MILLISECOND = 1_000L * 60 * 30;               // 30분

    public static final long REFRESH_TOKEN_TTL_MILLISECOND = 1_000L * 60 * 60 * 24 * 30;    // 30일

}
