package com.chaewsstore.util;

public class VerificationUtil {

    public static final String PASSWORD_REGEXP = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^\\w\\s]).{10,25}$";
    public static final String NICKNAME_REGEXP = "^[^\\s]{2,10}$";

}
