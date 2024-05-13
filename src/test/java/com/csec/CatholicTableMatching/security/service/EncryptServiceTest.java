package com.csec.CatholicTableMatching.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class EncryptServiceTest {

    @Value("${spring.security.symmetric.key}")
    private String key;

    // 주체
    EncryptService encryptService;

    // 협력자
    AesBytesEncryptor aesBytesEncryptor;

    // DI
    @BeforeEach
    void setup() {
        aesBytesEncryptor = new AesBytesEncryptor(key, "143950834095");
        encryptService = new EncryptService(aesBytesEncryptor);
    }

    @Test
    @DisplayName("암호화")
    void encrypt() {
        // given
        String password = "test_password";

        // when
        String encrypted = encryptService.encrypt(password);
        String result = encryptService.decrypt(encrypted);

        // then
        assertEquals(password, result);
    }

}