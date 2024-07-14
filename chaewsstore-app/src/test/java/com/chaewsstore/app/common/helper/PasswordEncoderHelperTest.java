package com.chaewsstore.app.common.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.chaewsstore.common.helper.PasswordEncoderHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderHelperTest {

    @InjectMocks
    private PasswordEncoderHelper passwordEncoderHelper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testEncodePassword() {
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

        String result = passwordEncoderHelper.encodePassword(rawPassword);

        assertEquals(encodedPassword, result);
        then(passwordEncoder).should(times(1)).encode(rawPassword);
    }

    @Test
    void testMatches() {
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        boolean result = passwordEncoderHelper.matches(rawPassword, encodedPassword);

        assertTrue(result);
        then(passwordEncoder).should(times(1)).matches(rawPassword, encodedPassword);
    }

    String rawPassword = "password123";
    String encodedPassword = "encodedPassword123";
}
