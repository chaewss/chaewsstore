package com.chaewsstore.app.common.util;

public class VerificationConstants {

    public static final String PASSWORD_REGEXP = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^\\w\\s]).{10,25}$";
    public static final String PASSWORD_MESSAGE = "비밀번호는 영문, 숫자, 특수문자 포함 10자 이상 25자 이하여야 합니다.";
    public static final String NICKNAME_REGEXP = "^[^\\s]{2,10}$";
    public static final String NICKNAME_MESSAGE = "닉네임은 2자 이상 10자 이하여야 합니다.";

}
