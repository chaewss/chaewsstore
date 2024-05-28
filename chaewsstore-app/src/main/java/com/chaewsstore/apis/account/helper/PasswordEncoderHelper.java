package com.chaewsstore.apis.account.helper;

import com.globalutils.annotation.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Helper
public class PasswordEncoderHelper {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
